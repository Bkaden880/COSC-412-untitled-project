import './App.css';
import MyCalendar from './components/MyCalendar';
import Navbar from './components/Navbar';
import LoginSignup from './components/pages/Login-Signup/LoginSignup';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';

function Home() {
  return (
    <div style={{ padding: 20 }}>
      <h2>Welcome</h2>
      <p>Use the navigation to open your calendar.</p>
    </div>
  );
}

function App() {
  return (
    <Router>
      <Navbar />
      <Routes>
        <Route path='/' element={<Home />} />
        <Route path='/My-Calendar' element={<MyCalendar />} />
        <Route path='/Login-Signup' element={<LoginSignup/>} />
      </Routes>
    </Router>
  );
}

export default App;
