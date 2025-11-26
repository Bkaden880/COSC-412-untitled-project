import React, { useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { AuthContext } from '../../../context/AuthContext';
import "./LoginSignup.css";

const LoginSignup = () => {
    const [action, setAction] = useState("Login");
    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);
    
    const { login, signup } = useContext(AuthContext);
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");
        setLoading(true);

        try {
            if (action === "Login") {
                if (!email || !password) {
                    throw new Error("Email and password required");
                }
                await login(email, password);
                navigate("/My-Calendar");
            } else {
                if (!name || !email || !password) {
                    throw new Error("Name, email, and password required");
                }
                await signup(name, email, password);
                navigate("/My-Calendar");
            }
        } catch (err) {
            setError(err.message || "Auth failed");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="container">
            <div className="header">
                <div className="text">{action}</div>
                <div className="underline"></div>
            </div>
            <form className="inputs" onSubmit={handleSubmit}>
                {action === "Login" ? null : (
                    <div className="input">
                        <input
                            type="text"
                            placeholder="Name"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                        />
                    </div>
                )}
                <div className="input">
                    <input
                        type="email"
                        placeholder="Email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                    />
                </div>
                <div className="input">
                    <input
                        type="password"
                        placeholder="Password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                </div>
                {error && <div className="error-message">{error}</div>}
            </form>
            <div className="submit-container">
                <button
                    className={action === "Login" ? "submit gray" : "submit"}
                    onClick={() => {
                        setAction("Sign Up");
                        setError("");
                        setName("");
                    }}
                    disabled={loading}
                >
                    Sign-Up
                </button>
                <button
                    className={action === "Sign Up" ? "submit gray" : "submit"}
                    onClick={() => {
                        setAction("Login");
                        setError("");
                    }}
                    disabled={loading}
                >
                    Login
                </button>
            </div>
            <button
                className="submit-btn"
                onClick={handleSubmit}
                disabled={loading}
            >
                {loading ? "Processing..." : (action === "Login" ? "Login" : "Sign Up")}
            </button>
        </div>
    );
}

export default LoginSignup;