import './App.css'
import {BrowserRouter, Navigate, Route, Routes} from "react-router-dom";
import {Login} from "./components/Login";
import {RequestPasswordReset} from "./components/RequestPasswordReset";
import {PasswordReset} from "./components/PasswordReset";
import {ProtectedRoute} from "./components/ProtectedRoute";
import ExerciseList from "./components/ExercisesList";
import Users from "./components/Users";

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/login" element={<Login/>} />
                <Route path="/password-reset/request" element={<RequestPasswordReset/>} />
                <Route path="/password-reset" element={<PasswordReset/>} />
                <Route path="/" element={
                    <ProtectedRoute>
                        <Navigate to="/exercises" />
                    </ProtectedRoute>
                } />
                <Route path="/exercises" element={
                    <ProtectedRoute>
                        <ExerciseList />
                    </ProtectedRoute>
                } />
                <Route path="/exercises/:exerciseId" element={
                    <ProtectedRoute>
                        <ExerciseList />
                    </ProtectedRoute>
                } />
                <Route path="/new-exercise" element={
                    <ProtectedRoute>
                        <ExerciseList />
                    </ProtectedRoute>
                } />
                <Route path="/exercise-lists" element={
                    <ProtectedRoute>
                        <h1>ToDo</h1>
                    </ProtectedRoute>
                } />
                <Route path="/users" element={
                    <ProtectedRoute>
                        <Users />
                    </ProtectedRoute>
                } />
            </Routes>
        </BrowserRouter>
    )
}

export default App