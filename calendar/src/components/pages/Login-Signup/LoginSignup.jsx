import React from 'react';
import "./LoginSignup.css";

const LoginSignup = () => {

    const [action,setaction] = React.useState("Login");


    return (
        <div className="container">
            <div className="header">
                <div className="text">{action}</div>
                <div className="underline"></div>
            </div>
            <div className="inputs">
                {action==="Login" ? null : <div className="input">
                    <input type ="text" placeholder='Name' />
                </div>}
                <div className="input">
                    <input type ="email" placeholder='Email' />
                </div>
                <div className="input">
                    <input type ="password" placeholder='Password' />
                </div>
            </div>
            {action==="Sign Up" ? null : <div className="forgot-password">Lost Password? <span>Click Here</span></div>}
            <div className="submit-container">
                <div className={action==="Login"?"submit gray":"submit"} onClick={()=>setaction("Sign Up")}>Sign-Up</div>
                <div className={action==="Sign Up"?"submit gray":"submit"} onClick={()=>setaction("Login")}>Login</div>
            </div>
        </div>          
    );
}

export default LoginSignup;