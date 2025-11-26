import React, { useState, useContext } from 'react';
import { AuthContext } from '../../../context/AuthContext';
import "./SyllabusUpload.css";

const API_UPLOAD = 'http://localhost:8081/api/syllabi/upload';

const SyllabusUpload = () => {
    const { user } = useContext(AuthContext);
    const [file, setFile] = useState(null);
    const [courseName, setCourseName] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [result, setResult] = useState(null);

    const handleFileChange = (e) => {
        setError('');
        setResult(null);
        const f = e.target.files && e.target.files[0];
        setFile(f);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setResult(null);

        if (!file) return setError('Please choose a PDF file to upload.');
        if (!courseName) return setError('Please provide a course name.');
        if (!user) return setError('You must be logged in to upload a syllabus.');

        const form = new FormData();
        form.append('file', file);
        form.append('userId', user.id);
        form.append('courseName', courseName);

        try {
            setLoading(true);
            const res = await fetch(API_UPLOAD, {
                method: 'POST',
                body: form,
            });

            if (!res.ok) {
                // try to read json error message
                let text = await res.text();
                try { const j = JSON.parse(text); if (j && j.message) text = j.message; } catch(_) {}
                throw new Error(text || `Upload failed: ${res.status}`);
            }

            const data = await res.json();
            setResult(data);
            setFile(null);
            setCourseName('');
            // reset file input value
            const input = document.getElementById('syllabus-file-input');
            if (input) input.value = '';
        } catch (err) {
            console.error(err);
            setError(err.message || 'Upload failed');
        } finally {
            setLoading(false);
        }
    };

    if (!user) {
        return <div className="syllabus-container"><p>Please log in to upload a syllabus.</p></div>;
    }

    return (
        <div className="syllabus-container">
            <h2>Syllabus Upload</h2>
            <p className="user-info">Logged in as: <strong>{user.email}</strong></p>
            <form className="upload-form" onSubmit={handleSubmit}>
                <div className="form-row">
                    <label>PDF File</label>
                    <input id="syllabus-file-input" type="file" accept="application/pdf" onChange={handleFileChange} />
                </div>

                <div className="form-row">
                    <label>Course Name</label>
                    <input type="text" value={courseName} onChange={(e) => setCourseName(e.target.value)} placeholder="e.g. Intro to CS" />
                </div>

                <div className="form-actions">
                    <button type="submit" className="btn btn-primary" disabled={loading}>{loading ? 'Uploading…' : 'Upload Syllabus'}</button>
                </div>
            </form>

            {error && <div className="upload-error">Error: {error}</div>}

            {result && (
                <div className="upload-result">
                    <h3>Upload Result</h3>
                    {result.aiGeneratedSummary && (
                        <div>
                            <h4>AI Summary</h4>
                            <div className="summary">
                                {result.aiGeneratedSummary.split('\\n').map((line, i) => {
                                    // skip empty lines and lines with "Text Length"
                                    if (!line.trim() || line.includes('Text Length')) {
                                        return null;
                                    }
                                    return <p key={i}>{line}</p>;
                                })}
                            </div>
                        </div>
                    )}
                    {result.studyPlan && (
                        <div>
                            <h4>Study Plan</h4>
                            <p><strong>Strategy:</strong> {result.studyPlan.overallStrategy}</p>
                            <p><strong>Estimated Hours:</strong> {result.studyPlan.estimatedStudyHours}</p>
                            <p><strong>Difficulty:</strong> {result.studyPlan.difficultyAssessment}</p>
                        </div>
                    )}
                </div>
            )}
        </div>
    );
}

export default SyllabusUpload;