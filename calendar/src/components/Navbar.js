import React, {useState, useEffect} from 'react'
import { Link } from 'react-router-dom'
import './Navbar.css'

function Navbar() {
    const [click, setClick] = useState(false)
    const [button, setButton] = useState(true)

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


    window.addEventListener('resize', showButton)

  return (
    <nav className="navbar">
        <div className="navbar-container">
            <Link to='/' className='navbar-logo' onClick={()=> setClick(false)}>
                CalPal <i className="fa-regular fa-calendar"></i>
            </Link>
            <div className="menu-icon" onClick ={()=> setClick(!click)}>
                <i className={click ? 'fas fa-times' : 'fas fa-bars'} />  
            </div>
            <ul className={click ? 'nav-menu active' : 'nav-menu'}>
                <li className='nav-item'>
                    <Link to='/My-Calendar' className='nav-links' onClick={()=> setClick(false)}>
                        My Calendar
                    </Link>
                </li>
                <li className='nav-item'>
                    <Link to='/Login-Signup' className='nav-links' onClick={()=> setClick(false)}>
                        Login
                    </Link>
                </li>
                {/* (removed duplicate mobile-only Login entry) */}
            </ul>
        </div>
    </nav>
  )
}

export default Navbar