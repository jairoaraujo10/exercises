import {Button, Card, Col, Form, Input, message, Row} from "antd";
import {UserOutlined} from "@ant-design/icons";
import {RequestResetPasswordRequest} from "../../context/AuthContext/utils.tsx";
import {useNavigate} from "react-router-dom";

export const RequestPasswordReset = () => {
    const navigate = useNavigate();

    const onFinish = async (values: { email: string }) => {
        const response = await RequestResetPasswordRequest(values.email)
        if(response && response.status === 204) {
            message.success('Password reset url sent successfully')
            navigate("/")
        } else {
            message.error('An error occurred while sending password reset url')
        }
    };

    return (
        <Row justify="center">
            <Card style={{ marginTop: 45 }}>
                <Col span={24} className="typo-grey typo-center">
                    <h2>Reset Password</h2>
                </Col>
                <Form
                    name="request_password_reset"
                    className="password-reset-form"
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

                    <Form.Item>
                        <Button type="primary" htmlType="submit" className="reset-form-button">
                            Send password reset email
                        </Button>
                    </Form.Item>
                </Form>
            </Card>
        </Row>
    );
}