import React, {createContext, ReactNode, useCallback, useEffect, useState} from "react";
import {getUserLocalStorate, LoginRequest, setUserLocalStorate} from "./utils.tsx";
import {message} from "antd";
import {jwtDecode} from "jwt-decode";

export enum AuthStatus {
    Loading,
    SignedIn,
    SignedOut
}

export interface IAuth {
    selfUserId: string | null;
    authStatus: AuthStatus;
    userRoles: string[];
    signIn?: (email: string, password: string) => void;
    signOut?: () => void;
}

const defaultState: IAuth = {
    selfUserId: null,
    authStatus: AuthStatus.Loading,
    userRoles: []
};
export const AuthContext = createContext<IAuth>(defaultState);

interface AuthProviderProps {
    children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
    const [selfUserId, setSelfUserId] = useState<string | null>(null);
    const [authStatus, setAuthStatus] = useState<AuthStatus>(AuthStatus.Loading);
    const [userRoles, setUserRoles] = useState<string[]>([]);

    function updateCurrentUser() {
        const user = getUserLocalStorate();
        if (user && user.token) {
            setAuthStatus(AuthStatus.SignedIn);
            setSelfUserId(user.id)
            setUserRoles(user.roles);
        } else {
            setAuthStatus(AuthStatus.SignedOut);
            setSelfUserId(null)
            setUserRoles([]);
        }
    }

    useEffect(() => {
        updateCurrentUser();
    }, []);

    const signIn = useCallback(async (email: string, password: string) => {
        const response = await LoginRequest(email, password);
        if (response.data && response.data.token) {
            const token = response.data.token
            const decodedToken = jwtDecode<{ roles: string, sub: string }>(token);
            const id = decodedToken.sub
            const roles = decodedToken.roles.split(',')
            setUserLocalStorate({
                id: id,
                token: token,
                roles: roles
            });
            message.success("Signed in");
        } else {
            if(response.error) {
                message.error(response.error.message)
            } else {
                message.error("Unknown error")
            }
        }
        updateCurrentUser()
    }, []);

    const signOut = useCallback(() => {
        setUserLocalStorate(null);
        updateCurrentUser()
        message.info("Signed out").then();
    }, []);

    return (
        <AuthContext.Provider value={{ selfUserId: selfUserId, authStatus, userRoles: userRoles, signIn, signOut }}>
            {children}
        </AuthContext.Provider>
    );
};