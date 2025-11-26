import './App.css';
import MyCalendar from './components/pages/Calendar/MyCalendar';
import Navbar from './components/Navbar';
import LoginSignup from './components/pages/Login-Signup/LoginSignup';
import SyllabusUpload from './components/pages/Syllabus/SyllabusUpload';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, AuthContext } from './context/AuthContext';
import { useContext } from 'react';

const ProtectedRoute = ({ element }) => {
  const { user, loading } = useContext(AuthContext);
  if (loading) return <div>Loading...</div>;
  return user ? element : <Navigate to="/Login-Signup" />;
};

function App() {
  return (
    <AuthProvider>
      <Router>
        <Navbar />
        <Routes>
          <Route path='/' element={<Navigate to="/Login-Signup" />} />
          <Route path='/My-Calendar' element={<ProtectedRoute element={<MyCalendar />} />} />
          <Route path='/Login-Signup' element={<LoginSignup/>} />
          <Route path='/Syllabus-Upload' element={<ProtectedRoute element={<SyllabusUpload/>} />} />
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;
