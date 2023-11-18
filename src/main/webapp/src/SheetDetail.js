import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';

function SheetDetail() {
  const { sheetId } = useParams();

  // Remplacez ces données factices par des données de feuille Excel depuis le backend
  const initialTableData = Array.from({ length: 50 }, () => Array(26).fill(''));
  initialTableData[0][0] = 'Titre col';
  initialTableData[0][1] = 'Titre col';
  initialTableData[0][2] = 'Titre col';
  initialTableData[1][0] = 'Donnée A1';
  initialTableData[1][1] = 'Donnée B1';
  initialTableData[1][2] = 'Donnée C2';
  const [tableData, setTableData] = useState([initialTableData]);
  // useEffect(() => {
  //   fetch('/${sheetId}/${sheetName}')
  //   .then(response => response.json())
  //   .then(data => setTableData(data))
  //   .catch(error => console.error('Error:', error));
  // }, []);

  const handleCellChange = (rowIndex, colIndex, value) => {
    const updatedTableData = [...tableData];
    updatedTableData[rowIndex][colIndex] = value;
    setTableData(updatedTableData);
  };

  const saveChanges = () => {
    // Exemple : fetch(`http://votre-backend/api/sheets/${sheetId}`, {
    //   method: 'PUT',
    //   body: JSON.stringify(tableData),
    //   headers: {
    //     'Content-Type': 'application/json'
    //   }
    // });
  }

  return (
    <div className="container-fluid p-0">
      <header className="p-3 bg-purple text-white d-flex align-items-center">
        <a className="nav-link text-white" href="/">
          <img src="/wexcel_white_on_black_soft.PNG" alt="Logo" height="50" className="mr-3" />
        </a>
        <h1 className="">Feuille {sheetId}</h1>
        <nav className="ml-auto">
          <ul className="nav">
            <li className="nav-item">
              <button className="btn btn-light" onClick={saveChanges}>Save</button>
            </li>
          </ul>
        </nav>
      </header>
      <table className="table">
        <thead>
          <tr>
            <th>ID</th>
            {Array.from({ length: 26 }, (_, i) => (
              <th key={i}>{String.fromCharCode(65 + i)}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {tableData.map((rowData, rowIndex) => (
            <tr key={rowIndex}>
              <th scope="row">{rowIndex + 1}</th>
              {rowData.map((cellValue, colIndex) => (
                <td key={colIndex}>
                  {cellValue && (
                    <input
                      type="text"
                      value={cellValue}
                      onChange={(e) =>
                        handleCellChange(rowIndex, colIndex, e.target.value)
                      }
                    />
                  )}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default SheetDetail;
