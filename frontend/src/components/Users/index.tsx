import {Button, Form, Input, Layout, message, Modal, Space, Switch, Table} from "antd";
import {PlusOutlined} from "@ant-design/icons";
import {ColumnsType} from "antd/es/table";
import React, {useCallback, useContext, useEffect, useState} from "react";
import User from "../../interfaces/User.tsx";
import {CreateUser, DeleteUser, ListUsers} from "../../context/AuthContext/utils.tsx";
import {AuthContext} from "../../context/AuthContext";

const { Content } = Layout;
const { Search } = Input;

interface UserList {
    users: User[]
    total: number;
}

const Users: React.FC = () => {
    const {selfUserId} =  useContext(AuthContext);
    const [userList, setUserList] = useState<UserList>({
        users: [],
        total: 0,
    });
    const [loading, setLoading] = useState(false);
    const [open, setOpen] = useState<boolean>(false);
    const [searchTerm, setSearchTerm] = useState<string>('');

    const [currentPage, setCurrentPage] = useState<number>(1);
    const pageSize = 20;

    const fetchUsers = useCallback( async () => {
        try {
            setLoading(true)
            const response = await ListUsers(pageSize, currentPage, searchTerm);
            if (response && response.status === 200)
                setUserList(response.data);
        } catch (error) {
            console.error('Error fetching exercises:', error);
        } finally {
            setLoading(false)
        }
    }, [pageSize, currentPage, searchTerm]);

    useEffect(() => {
        fetchUsers();
    }, [fetchUsers]);

    const handleAddUser = () => {
        setOpen(true);
    };

    const addUser = async (user: User) => {
        const response = await CreateUser(user)
        if (response && response.status === 201) {
            message.success("User created");
            setOpen(false);
            await fetchUsers()
        } else {
            message.error("Failed to create user");
        }
    };

    const handleDeleteUser = async (user: User) => {
        const response = await DeleteUser(user)
        if (response && response.status === 204) {
            message.success("User deleted");
            setUserList({
                users: userList.users.filter(u => u.id !== user.id),
                total: userList.total - 1,
            });
        } else {
            message.error("Failed to delete user");
        }
    };

    const handleAdminToggle = (userId: string) => {
        setUserList({
            users: userList.users.map(user =>
                user.id === userId
                    ? {
                        ...user,
                        roles: user.roles.includes('ADMIN')
                            ? user.roles.filter(role => role !== 'ADMIN')
                            : [...user.roles, 'ADMIN']
                    }
                    : user
            ),
            total: userList.total,
        });
    };

    const handleSearch = (value: string) => {
        setSearchTerm(value);
        setCurrentPage(1);
    };

    const handleChangePage = (page: number) => {
        setCurrentPage(page);
    };

    const columns: ColumnsType<User> = [
        {
            title: 'Email',
            dataIndex: 'email',
            key: 'email',
        },
        {
            title: 'Name',
            dataIndex: 'name',
            key: 'name',
        },
        {
            title: 'Admin',
            key: 'isAdmin',
            render: (_, user) => (
                <Switch
                    checked={user.roles.includes('ADMIN')}
                    onChange={() => handleAdminToggle(user.id!)}
                    disabled={user.id === selfUserId}
                />
            ),
        },
        {
            title: 'Action',
            key: 'action',
            render: (_, user) => (
                <Space size="middle">
                    <Button onClick={() => handleDeleteUser(user)} danger
                            disabled={user.id === selfUserId}>
                        Delete
                    </Button>
                </Space>
            ),
        },
    ];

    return (
        <>
            <Layout className="site-layout">
                <Content style={{ margin: '0 16px' }}>
                    <div style={{ margin: '16px 0' }}>
                        <Search
                            placeholder="Search by email"
                            enterButton="Search"
                            onSearch={handleSearch}
                            style={{ width: 300 }}
                        />
                        <Button type="primary" icon={<PlusOutlined/>} style={{ float: 'right' }} onClick={handleAddUser}>
                            Add User
                        </Button>
                    </div>
                    <Table
                        rowKey={(user) => user.email}
                        columns={columns}
                        dataSource={userList.users}
                        pagination={{
                            current: currentPage,
                            pageSize: pageSize,
                            total: userList.total,
                            onChange: handleChangePage,
                        }}
                        loading={loading}/>
                </Content>
            </Layout>
            <Modal
                title={'Add User'}
                open={open}
                onCancel={() => setOpen(false)}
                footer={null}
            >
                <Form
                    initialValues={{ id: null, name: '', email: '' }}
                    onFinish={addUser}
                >
                    <Form.Item
                        label="Email"
                        name="email"
                        rules={[
                            { required: true, message: 'Email is required!' },
                            { type: 'email', message: 'Must be a valid email' }
                        ]}
                    >
                        <Input/>
                    </Form.Item>
                    <Form.Item
                        label="Name"
                        name="name"
                        rules={[
                            { required: true, message: 'Please input the name!' }
                        ]}
                    >
                        <Input/>
                    </Form.Item>
                    <Form.Item>
                        <Button type="primary" htmlType="submit">
                            Add
                        </Button>
                    </Form.Item>
                </Form>
            </Modal>
        </>
    );
};

export default Users;