import {Button, Card, Col, Form, Input, message, Row} from "antd";
import {useLocation, useNavigate} from "react-router-dom";
import {useEffect, useState} from "react";
import {ResetPasswordRequest} from "../../context/AuthContext/utils.tsx";
import {jwtDecode} from "jwt-decode";

export const PasswordReset = () => {
    const location = useLocation();
    const [token, setToken] = useState<string | null>();
    const navigate = useNavigate();

    useEffect(() => {
        try {
            const token = new URLSearchParams(location.search).get('token');
            if (!token) {
                message.error("Invalid URL!")
                navigate("/")
                return;
            }

            let decodedToken;
            try {
                decodedToken = jwtDecode(token);
            } catch (error) {
                message.error("Invalid reset password URL!")
                navigate("/")
                return;
            }

            if (!decodedToken || !decodedToken.exp) {
                message.error("Invalid reset password URL!")
                navigate("/")
                return;
            }

            const currentDate = new Date();
            if (decodedToken.exp * 1000 < currentDate.getTime()) {
                message.error("Reset password link expired!")
                navigate("/")
                return;
            }

            setToken(token);
        } catch (error) {
            console.error("Unexpected error:", error);
        }
    }, [location.search, navigate]);

    async function onFinish(values: { password: string }) {
        if (!token) {
            message.error("Failed to reset password!");
            return;
        }

        try {
            const response = await ResetPasswordRequest(token, values.password);
            if (response && response.status === 200) {
                message.success("Password reset successful");
                navigate("/login");
            } else {
                message.error("Failed to reset password!");
            }
        } catch (error) {
            message.error("Failed to reset password!");
            console.error("Reset password error:", error);
        }
    }

    return (
        <Row justify="center">
            <Card style={{ marginTop: 45 }}>
                <Col span={24} className="typo-grey typo-center">
                    <h2>Reset Password</h2>
                </Col>
                <Form
                    name="password_reset"
                    className="password-reset-form"
                    initialValues={{ remember: false }}
                    autoComplete="off"
                    onFinish={onFinish}
                    layout="vertical"
                >
                    <Form.Item
                        name="password"
                        rules={[
                            {
                                required: true, message: 'Please input your Password!'
                            },
                        ]}
                        style={{ marginBottom: 15 }}
                    >
                        <Input.Password placeholder="Enter your new password" />
                    </Form.Item>
                    <Form.Item
                        name="password2"
                        dependencies={['password']}
                        rules={[
                            {
                                required: true, message: 'Please confirm your Password!'
                            },
                            ({ getFieldValue }) => ({
                                validator(_, value) {
                                    if (!value || getFieldValue('password') === value) {
                                        return Promise.resolve();
                                    }
                                    return Promise.reject(new Error('The new password that you entered do not match!'));
                                },
                            }),
                        ]}
                        style={{ marginBottom: 15 }}
                    >
                        <Input.Password placeholder="Confirm your new password" />
                    </Form.Item>
                    <Form.Item>
                        <Button type="primary" htmlType="submit" className="password-reset-form-button" style={{ width: '100%' }}>
                            Submit
                        </Button>
                    </Form.Item>
                </Form>
            </Card>
        </Row>
    )
}