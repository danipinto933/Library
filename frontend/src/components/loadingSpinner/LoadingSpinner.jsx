import React from 'react';
import './LoadingSpinner.css';

function LoadingSpinner({ message = 'exportando libros...' }) {
  return (
    <div className="spinner-overlay" role="status" aria-live="polite">
      <div className="spinner-container">
        <div className="spinner-ring">
          <div></div>
          <div></div>
          <div></div>
          <div></div>
        </div>
        <p className="spinner-message">{message}</p>
      </div>
    </div>
  );
}

export default LoadingSpinner;
