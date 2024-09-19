import React, {ReactNode, useContext, useEffect} from 'react';

import {AuthContext, AuthStatus} from "../../context/AuthContext";
import {Navigate, useLocation, useNavigate} from "react-router-dom";
import {Api} from "../../context/AuthContext/utils.tsx";
import {Avatar, Button, Dropdown, Layout, Menu, MenuProps, message} from "antd";
import {FormOutlined, OrderedListOutlined, UserOutlined, LogoutOutlined} from "@ant-design/icons";

type MenuItem = Required<MenuProps>['items'][number];

interface MenuOptions {
    label: string;
    key: string;
    icon?: React.ReactNode;
    requiredRole?: string
}

const menuOptions: MenuOptions[] = [
    {
        label: 'Exercises',
        key: '/exercises',
        icon: <FormOutlined />,
        requiredRole: 'USER'
    },
    {
        label: 'Exercise Lists',
        key: '/exercise-lists',
        icon: <OrderedListOutlined />,
        requiredRole: 'USER'
    },
    {
        label: 'Users',
        key: '/users',
        icon: <UserOutlined />,
        requiredRole: 'ADMIN'
    }
]

interface ProtectedRouteProps {
    children: ReactNode;
}

export const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ children}) =>{
    const location = useLocation();
    const { authStatus, userRoles, signOut } = useContext(AuthContext);
    const navigate = useNavigate();

    useEffect(() => {
        const interceptor = Api.interceptors.response.use(
            response => response,
            error => {
                if (error.response && error.response.status === 403) {
                    message.error('Session expired. Logging out...');
                    if (signOut) signOut();
                    navigate('/login');
                }
                return Promise.reject(error);
            }
        );

        return () => {
            Api.interceptors.response.eject(interceptor);
        };
    }, [signOut, navigate]);

    if (authStatus === AuthStatus.Loading) {
        return <div>Loading...</div>;
    }

    const handleLogout = () => {
        if (signOut) {
            signOut();
            navigate('/login');
        }
    };

    if (authStatus === AuthStatus.SignedIn) {
        const menu = {
            items: [
                {
                    key: 1,
                    label: (
                            <Button key="logout" onClick={handleLogout} danger>
                                Logout <LogoutOutlined />
                            </Button>
                    )
                }
            ]
        };

        const handleMenuClick = (e: MenuItem) => {
            const selectedPath = menuOptions.find(item => item?.key === e?.key)?.key;
            if (selectedPath) {
                navigate(selectedPath?.toString());
            }
        };

        return (
            <Layout style={{ minHeight: '100vh' }}>
                <Layout.Header style={{
                    background: '#fff',
                    padding: '0 16px',
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center'
                }}>
                    <div style={{flex: 0.5}}>
                        <Menu
                            onClick={handleMenuClick}
                            selectedKeys={[location.pathname]}
                            mode="horizontal"
                            items={menuOptions.filter(option=>{
                                return (option.requiredRole && userRoles.find(value => value===option.requiredRole))
                            })}
                        />
                    </div>
                    <Dropdown menu={menu} placement="bottomRight" arrow>
                        <a className="ant-dropdown-link" onClick={e => e.preventDefault()}>
                            <Avatar icon={<UserOutlined/>}/>
                        </a>
                    </Dropdown>
                </Layout.Header>
                <Layout.Content style={{margin: '24px 16px'}}>
                    {/* Content */}
                    <div style={{ padding: 24, background: '#fff', minHeight: 360 }}>
                        {children}
                    </div>
                </Layout.Content>
                <Layout.Footer style={{ textAlign: 'center' }}>
                    {/* Footer content */}
                </Layout.Footer>
            </Layout>
        );
    }

    return <Navigate to="/login" replace={true}/>;
};
