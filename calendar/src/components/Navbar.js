import React, {useState, useEffect, useContext} from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { AuthContext } from '../context/AuthContext'
import './Navbar.css'

function Navbar() {
    const [click, setClick] = useState(false)
    const [button, setButton] = useState(true)
    const { user, loading, logout } = useContext(AuthContext)
    const navigate = useNavigate()

    const showButton = () => {
        if(window.innerWidth <= 960) {
            setButton(false)
        } else {
            setButton(true)
        }
    }

    useEffect(() => {
        showButton()
    }, [])

    const handleLogout = () => {
        logout()
        setClick(false)
        navigate('/Login-Signup')
    }

    window.addEventListener('resize', showButton)

  return (
    <nav className="navbar">
        <div className="navbar-container">
            <Link to={user ? '/My-Calendar' : '/Login-Signup'} className='navbar-logo' onClick={()=> setClick(false)}>
                CalPal <i className="fa-regular fa-calendar"></i>
            </Link>
            <div className="menu-icon" onClick ={()=> setClick(!click)}>
                <i className={click ? 'fas fa-times' : 'fas fa-bars'} />  
            </div>
            <ul className={click ? 'nav-menu active' : 'nav-menu'}>
                {!loading && user && (
                    <>
                        <li className='nav-item'>
                            <Link to='/Syllabus-Upload' className='nav-links' onClick={()=> setClick(false)}>
                                Upload Syllabus
                            </Link>
                        </li>
                        <li className='nav-item'>
                            <Link to='/My-Calendar' className='nav-links' onClick={()=> setClick(false)}>
                                My Calendar
                            </Link>
                        </li>
                        <li className='nav-item'>
                            <button className='nav-links logout-btn' onClick={handleLogout}>
                                Logout
                            </button>
                        </li>
                    </>
                )}
                {!loading && !user && (
                    <li className='nav-item'>
                        <Link to='/Login-Signup' className='nav-links' onClick={()=> setClick(false)}>
                            Login
                        </Link>
                    </li>
                )}
            </ul>
        </div>
    </nav>
  )
}

export default Navbar