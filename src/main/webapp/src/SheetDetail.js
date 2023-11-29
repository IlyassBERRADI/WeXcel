import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';

function SheetDetail() {

  const { sheetId } = useParams();

  const [content, setContent] = useState([]);

  const [contentChanged, setContentChanged] = useState(false);


  useEffect(() => {
    console.log('useEffect');
    fetch(`/api/content/${sheetId}`)
      .then(response => {
        if (!response.ok) {
          throw new Error(`Request failed with status: ${response.status}`);
        }
        return response.json();
      })
      .then(data => setContent(data))
      .catch(error => console.error('Error:', error));
      setContentChanged(false);
  }, [sheetId, contentChanged]);

  const handleInputChange = (column, rowIndex, value) => {
    // Met à jour l'état local
    const updatedContent = [...content];
    updatedContent.forEach((col) => {
      col.values[rowIndex] = col === column ? value : col.values[rowIndex];
    });
    setContent(updatedContent);
  };

  const handleCellValueChange = async (column, rowIndex, value) => {
    try {
      // Effectue la requête POST vers l'API
      if (!value.trim()) {
        value = null;
      }
      const response = await fetch(`/api/updateCell/${sheetId}/${column.name}/${rowIndex + 1}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: value,
      });

      // Vérifie si la requête a réussi
      if (!response.ok) {
        throw new Error(`Erreur lors de la mise à jour de la cellule : ${response.statusText}`);
      }
      handleInputChange(column, rowIndex, value);
    } catch (error) {
      console.error('Erreur lors de la mise à jour de la cellule :', error.message);
    }
  };

  const addRow = async () => {
    try {
      // Effectue la requête POST vers l'API
      const response = await fetch(`/api/addRow/${sheetId}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error(`Erreur lors de l'ajout de la ligne : ${response.statusText}`);
      }
      setContentChanged(true);


    } catch (error) {
      console.error('Erreur lors de l\'ajout de la ligne :', error.message);
    }
  };

  const [newColumnName, setNewColumnName] = useState('');

  const addColumn = async () => {
    try {
      // Vérifie que le nom de la colonne n'est pas vide avant de faire la requête
      if (!newColumnName.trim()) {
        console.error('Le nom de la colonne ne peut pas être vide.');
        return;
      }

      // Effectue la requête POST vers l'API
      const response = await fetch(`/api/addColumn/${sheetId}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          name: newColumnName,
          type: 'VARCHAR(255)',
          values: []
        }),
      });

      if (!response.ok) {
        throw new Error(`Erreur lors de l'ajout de la colonne : ${response.statusText}`);
      }
      setNewColumnName('')
      setContentChanged(true);

    } catch (error) {
      console.error('Erreur lors de l\'ajout de la colonne :', error.message);
    }
  };

  const handleNewColumnChange = (e) => {
    setNewColumnName(e.target.value);
  };

  const handleInputKeyDown = (e) => {
    // Vérifie si la touche pressée est la touche "Entrée" (code 13)
    if (e.key === 'Enter') {
      addColumn();
    }
  };


  return (
    <div className="container-fluid p-0 ">
      <header className="p-3 bg-purple text-white d-flex align-items-center">
        <a className="nav-link text-white" href="/">
          <img src="/wexcel_white_on_black_soft.PNG" alt="Logo" height="50" className="mr-3" />
        </a>
        <h1 className="">Feuille {sheetId}</h1> // TODO: récupérer le nom de la feuille
      </header>
      <div className="container ms-0 p-0">
        <table className="table table-striped">
          <thead>
            <tr>
              <th>ID</th>
              {content.map((column, index) => (
                <th className="text-center" key={index}>
                  {column.name}
                  <span className="type">{column.type}</span>
                </th>
              ))}
              <th className="text-center">
                <input className="text-center"
                  type="text" placeholder="Entrer pour valider"
                  value={newColumnName}
                  onChange={handleNewColumnChange}
                  onKeyDown={handleInputKeyDown} />
              </th>
            </tr>
          </thead>
          <tbody>
            {content.length > 0 &&
              content[0].values.map((_, rowIndex) => (
                <tr key={rowIndex}>
                  <th scope="row">{rowIndex + 1}</th>
                  {content.map((column, colIndex) => (
                    <td className="text-center" key={colIndex}>
                      <input
                        type="text"
                        value={column.values[rowIndex] || ""}
                        onChange={(e) => handleInputChange(column, rowIndex, e.target.value)}
                        onBlur={(e) => handleCellValueChange(column, rowIndex, e.target.value)}
                      />
                    </td>
                  ))}
                </tr>
              ))}
          </tbody>
        </table>
        <button className="btn btn-light bg-purple" onClick={addRow}>+</button>
      </div>
    </div>

  );

}

export default SheetDetail;
