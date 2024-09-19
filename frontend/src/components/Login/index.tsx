import { LockOutlined, UserOutlined } from '@ant-design/icons';
import {Button, Card, Col, Form, Input, Row} from "antd";
import {AuthContext, AuthStatus} from "../../context/AuthContext";
import {useContext, useEffect} from "react";
import {useNavigate} from "react-router-dom";

export const Login = () => {
    const { signIn, authStatus } = useContext(AuthContext);
    const navigate = useNavigate();

    const onFinish = (values: { email: string, password: string }) => {
        if (signIn) {
            signIn(values.email, values.password);
        }
    };

    useEffect(() => {
        if (authStatus === AuthStatus.SignedIn) {
            navigate('/');
        }
    }, [authStatus, navigate]);

    return (
        <Row justify="center">
            <Card style={{ marginTop: 45 }}>
                <Col span={24} className="typo-grey typo-center">
                    <h2>Sign in</h2>
                </Col>
                <Form
                    name="normal_login"
                    className="login-form"
                    initialValues={{ remember: true }}
                    onFinish={onFinish}
                    layout="vertical"
                >
                    <Form.Item
                        name="email"
                        rules={[
                            { required: true, message: 'Email is required!' },
                            { type: 'email', message: 'Must be a valid email' }
                        ]}
                        style={{ marginBottom: 15 }}
                    >
                        <Input prefix={<UserOutlined className="site-form-item-icon" />} placeholder="Enter your email address" />
                    </Form.Item>
                    <Form.Item
                        name="password"
                        rules={[
                            { required: true, message: 'Password is required!' },
                        ]}
                        style={{ marginBottom: 5 }}
                    >
                        <Input
                            prefix={<LockOutlined className="site-form-item-icon" />}
                            type="password"
                            placeholder="Enter your password"
                        />
                    </Form.Item>
                    <Form.Item>
                        <a className="login-form-forgot" href={"/password-reset/request"}>
                            Forgot password
                        </a>
                    </Form.Item>

                    <Form.Item>
                        <Button type="primary" htmlType="submit" className="login-form-button">
                            Sign in
                        </Button>
                    </Form.Item>
                </Form>
            </Card>
        </Row>
    );
}