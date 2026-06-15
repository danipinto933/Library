import React from 'react';
import './Pagination.css';

const Pagination = ({ currentPage, totalPages, prevPage, nextPage }) => {
  return (
    <div className="pagination">
      <button 
        onClick={prevPage} 
        disabled={currentPage === 1}
      >
        Anterior
      </button>
      
      <span style={{ margin: '0 15px' }}>
        Página {currentPage} de {totalPages || 1}
      </span>
      
      <button 
        onClick={nextPage} 
        disabled={currentPage >= totalPages} 
      >
        Siguiente
      </button>
    </div>
  );
};

export default Pagination;