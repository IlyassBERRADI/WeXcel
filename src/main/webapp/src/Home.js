import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.css';
import './index.css';

function Home() {
  const [sheets, setSheets] = useState([]);
  const [contentChanged, setContentChanged] = useState(false);
  useEffect(() => {
    fetch('/api/references')
      .then(response => response.json())
      .then(data => setSheets(data))
      .catch(error => console.error('Error:', error));
    setContentChanged(false);
  }, [contentChanged]);

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

  const [newSheetName, setNewSheetName] = useState('');

  const createNewSheet = async () => {
    try {
      // Vérifie que le nom de la colonne n'est pas vide avant de faire la requête
      if (!newSheetName.trim()) {
        console.error('Le nom de la colonne ne peut pas être vide.');
        return;
      }

      // Effectue la requête POST vers l'API
      const response = await fetch(`/api/create`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          name: newSheetName,
          creationDate: new Date(),
          lastModificationDate: new Date()
        }),
      });

      if (!response.ok) {
        throw new Error(`Erreur lors de l'ajout de la colonne : ${response.statusText}`);
      }
      setNewSheetName('')
      setContentChanged(true);

    } catch (error) {
      console.error('Erreur lors de l\'ajout de la colonne :', error.message);
    }
  };

  const handleNewSheetChange = (e) => {
    setNewSheetName(e.target.value);
  };

  const handleInputKeyDown = (e) => {
    // Vérifie si la touche pressée est la touche "Entrée" (code 13)
    if (e.key === 'Enter') {
      createNewSheet();
    }
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
                  <strong>Date de création :</strong> {new Date(sheet.creationDate).toLocaleDateString()} <br />
                  <strong>Date de dernière modification :</strong> {new Date(sheet.lastModificationDate).toLocaleDateString()} <br />
                </li>
              </Link>
            ))}
          </ul>
          <input className="text-center p-2 mt-3 "
            type="text" placeholder="Entrer pour valider"
            value={newSheetName}
            onChange={handleNewSheetChange}
            onKeyDown={handleInputKeyDown} />
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
