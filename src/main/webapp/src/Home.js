import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.css';
import './index.css';

function Home() {
  const [sheets, setSheets] = useState([]);

  useEffect(() => {
    // Simulation des données en attendant les requêtes HTTP
    const fakeData = [
      { id: 1, name: 'Feuille 1', createdAt: '2023-01-01', lastModified: '2023-01-05' },
      { id: 2, name: 'Feuille 2', createdAt: '2023-02-10', lastModified: '2023-02-15' },
      { id: 3, name: 'Feuille 3', createdAt: '2023-03-20', lastModified: '2023-03-25' },
      { id: 4, name: 'Feuille 4', createdAt: '2023-04-30', lastModified: '2023-04-30' },
      { id: 5, name: 'Feuille 5', createdAt: '2023-05-10', lastModified: '2023-05-15' },
      { id: 6, name: 'Feuille 6', createdAt: '2023-06-20', lastModified: '2023-06-25' },
      { id: 7, name: 'Feuille 7', createdAt: '2023-07-30', lastModified: '2023-07-30' },
      { id: 8, name: 'Feuille 8', createdAt: '2023-08-10', lastModified: '2023-08-15' },
      { id: 9, name: 'Feuille 9', createdAt: '2023-09-20', lastModified: '2023-09-25' },
      { id: 10, name: 'Feuille 10', createdAt: '2023-10-30', lastModified: '2023-10-30' },
      { id: 11, name: 'Feuille 11', createdAt: '2023-11-10', lastModified: '2023-11-15' },
      { id: 12, name: 'Feuille 12', createdAt: '2023-12-20', lastModified: '2023-12-25' },

    ];
    // Mettre à jour l'état avec les données factices
    setSheets(fakeData);
    // Effectuer un appel HTTP pour récupérer les données des feuilles Excel sous forme de JSON
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
              <Link to={`/sheets/${sheet.id}`} key={sheet.id}>
                <li className="list-group-item">
                  <strong>Nom :</strong> {sheet.name} <br />
                  <strong>Date de création :</strong> {sheet.createdAt} <br />
                  <strong>Date de dernière modification :</strong> {sheet.lastModified} <br />
                </li>
              </Link>
            ))}

          </ul>
        </main>
      </div>
      <footer className="footer fixed-bottom text-center p-3">
        <div className="container">
          <div className="btn-group">
            <button type="button" className="btn btn-light" onClick={() => handleSortByName()}>Trier par nom</button>
            <button type="button" className="btn btn-light" onClick={() => handleSortByCreationDate()}>Trier par date de création</button>
            <button type="button" className="btn btn-light" onClick={() => handleSortByModificationDate()}>Trier par date de modification</button>
          </div>
        </div>
      </footer>
    </div>
  );
}

export default Home;
