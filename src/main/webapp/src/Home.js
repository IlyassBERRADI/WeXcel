import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.css';
import './index.css';

function Home() {
  const [sheets, setSheets] = useState([]);

  useEffect(() => {
    fetch('/table/tables')
    .then(response => response.json())
    .then(data => setSheets(data))
    .catch(error => console.error('Error:', error));
  }, []);

  // Fonctions de tri
  const handleSortByName = () => {
    const sortedSheets = [...sheets].sort((a, b) => a.name.localeCompare(b.name));
    setSheets(sortedSheets);
  };
  const handleSortByCreationDate = () => {
    const sortedSheets = [...sheets].sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt));
    setSheets(sortedSheets);
  };
  const handleSortByModificationDate = () => {
    const sortedSheets = [...sheets].sort((a, b) => new Date(a.lastModified) - new Date(b.lastModified));
    setSheets(sortedSheets);
  };

  return (
    <div className="container-fluid p-0">
      <header className="p-3 bg-purple text-white d-flex align-items-center">
        <img src="/icon_blanc.png" alt="Logo" width="50" height="50" className="mr-3" />
        <h1 className="mb-0">WeXcel</h1>
        <nav className="ml-auto">
          <ul className="nav">
            <li className="nav-item">
              <a className="nav-link text-white" href="/">Accueil</a>
            </li>
            <li className="nav-item">
              <a className="nav-link text-white" href="/about">À propos</a>
            </li>
          </ul>
        </nav>
      </header>
      <div className="container ">
        <main className="p-4">
          <h2>Vos feuilles</h2>
          <ul className="list-group">
            {sheets.map(sheet => (
              <Link to={`/sheets/${sheet.id}/${sheet.name}`} key={sheet.id}>
                <li className="list-group-item">
                  <strong>Nom :</strong> {sheet.name} <br />
                  <strong>Date de création :</strong> {sheet.creationDate} <br />
                  <strong>Date de dernière modification :</strong> {sheet.modificationDate} <br />
                </li>
              </Link>
            ))}

          </ul>
        </main>
      </div>
      <footer className="footer fixed-bottom text-center p-3">
        <div className="container">
          <div className="btn-group">
            <button type="button" className="btn btn-light bg-purple" onClick={() => handleSortByName()}>Trier par nom</button>
            <button type="button" className="btn btn-light bg-purple" onClick={() => handleSortByModificationDate()}>Trier par date de modification</button>
            <button type="button" className="btn btn-light bg-purple" onClick={() => handleSortByCreationDate()}>Trier par date de création</button>
          </div>
        </div>
      </footer>
    </div>
  );
}

export default Home;
