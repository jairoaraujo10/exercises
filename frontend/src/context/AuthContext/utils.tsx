import axios, {AxiosError} from "axios";
import {FullExercise, TagModel} from "../../interfaces/Exercise.tsx";
import {jwtDecode} from "jwt-decode";
import User from "../../interfaces/User.tsx";

export const Api = axios.create({
    baseURL: "http://localhost/api"
});

export interface Message {
    message: string
}

export interface LoginResponse {
    token: string
}

export interface ApiResult<T> {
    data?: T
    error?: Message
}

export interface IUser {
    id: string;
    token: string;
    roles: string[];
}

export const setUserLocalStorate = (user: IUser | null) => {
    if (user) {
        localStorage.setItem('u', user.token);
    } else {
        localStorage.removeItem('u')
    }
};

export const getUserLocalStorate = (): IUser | null => {
    const token = localStorage.getItem('u');
    if (!token) {
        return null;
    }
    const decodedToken = jwtDecode<{ roles: string, sub: string }>(token);
    const id = decodedToken.sub
    const roles = decodedToken.roles.split(',')
    return {
        id: id,
        token: token,
        roles: roles
    }
};

export async function LoginRequest(email: string, password: string): Promise<ApiResult<LoginResponse>> {
    try {
        const request = await Api.post('/auth/login', { email, password });
        return {data: request.data};
    } catch (e) {
        if (e instanceof AxiosError) {
            const error: AxiosError<Message> = e
            if(error.response) {
                return {error: error.response.data};
            }
        }
        return {error: {message: "unknown error"}};
    }
}

export async function RequestResetPasswordRequest(email: string) {
    try {
        const request = await Api.post('/auth/reset-password/request', { email });
        return {"data": request.data, "status": request.status};
    } catch (error) {
        console.error(error);
        return null;
    }
}

export async function ResetPasswordRequest(token: string, password: string) {
    try {
        const config = {
            headers: { Authorization: `Bearer ${token}` }
        };
        const request = await Api.post('/auth/reset-password', { password }, config);
        return {"data": request.data, "status": request.status};
    } catch (error) {
        console.error(error);
        return null;
    }
}

export async function ListUsers(pageSize: number, currentPage: number, searchTerm: string) {
    try {
        const user = getUserLocalStorate();
        const config = {
            headers: { Authorization: `Bearer ${user?.token}` }
        };
        const request = await Api.post(`/user/search?limit=${pageSize}&offset=${(currentPage - 1) * pageSize}`, {
            searchTerm: searchTerm
        }, config);
        return {"data": request.data, "status": request.status};
    } catch (error) {
        console.error(error);
        return null;
    }
}

export async function CreateUser(user: User) {
    try {
        const requester = getUserLocalStorate();
        const config = {
            headers: { Authorization: `Bearer ${requester?.token}` }
        };
        const request = await Api.post(`/user`, user, config);
        return {"data": request.data, "status": request.status};
    } catch (error) {
        console.error(error);
        return null;
    }
}

export async function DeleteUser(user: User) {
    try {
        const requester = getUserLocalStorate();
        const config = {
            headers: { Authorization: `Bearer ${requester?.token}` }
        };
        const request = await Api.delete(`/user/${user?.id}`, config);
        return {"data": request.data, "status": request.status};
    } catch (error) {
        console.error(error);
        return null;
    }
}

export async function SearchExercisesRequest(pageSize: number, currentPage: number, searchTerm: string, tags: TagModel[]) {
    try {
        const user = getUserLocalStorate();
        const config = {
            headers: { Authorization: `Bearer ${user?.token}` }
        };
        const request = await Api.post(`/exercise/search?limit=${pageSize}&offset=${(currentPage - 1) * pageSize}`, {
            searchTerm: searchTerm,
            selectedTags: tags.map(tag => {
                return {
                    value: tag.value
                }
            })
        }, config);
        return {"data": request.data, "status": request.status};
    } catch (error) {
        console.error(error);
        return null;
    }
}

export async function GetExercise(id: string): Promise<{ data: FullExercise; status: number } | null> {
    try {
        const user = getUserLocalStorate();
        const config = {
            headers: { Authorization: `Bearer ${user?.token}` }
        };
        const request = await Api.get(`/exercise/${id}`, config);
        return {"data": request.data, "status": request.status};
    } catch (error) {
        console.error(error);
        return null;
    }
}

export async function CreateExercise(exercise: FullExercise): Promise<ApiResult<Message>> {
    try {
        const user = getUserLocalStorate();
        const config = {
            headers: { Authorization: `Bearer ${user?.token}` }
        };
        await Api.post(`/exercise`, exercise, config);
        return {data: {message: "Exercise created"}};
    } catch (e) {
        if (e instanceof AxiosError) {
            const error: AxiosError<Message> = e
            if (error.response) {
                return {error: error.response.data};
            }
        }
        return {error: {message: "unknown error"}};
    }
}

export async function UpdateExercise(exercise: FullExercise): Promise<ApiResult<Message>> {
    try {
        const user = getUserLocalStorate();
        const config = {
            headers: { Authorization: `Bearer ${user?.token}` }
        };
        await Api.put(`/exercise/${exercise.id}`, exercise, config);
        return {data: {message: "Exercise save"}};
    } catch (e) {
        if (e instanceof AxiosError) {
            const error: AxiosError<Message> = e
            if (error.response) {
                return {error: error.response.data};
            }
        }
        return {error: {message: "unknown error"}};
    }
}

export async function DeleteExercise(exercise: FullExercise): Promise<ApiResult<Message>> {
    try {
        const user = getUserLocalStorate();
        const config = {
            headers: { Authorization: `Bearer ${user?.token}` }
        };
        await Api.delete(`/exercise/${exercise.id}`, config);
        return {data: {message: "Exercise deleted"}};
    } catch (e) {
        if (e instanceof AxiosError) {
            const error: AxiosError<Message> = e
            if(error.response) {
                return {error: error.response.data};
            }
        }
        return {error: {message: "unknown error"}};
    }
}