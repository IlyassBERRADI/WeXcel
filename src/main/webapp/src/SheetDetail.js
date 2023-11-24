import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';

function SheetDetail() {
  const { sheetId, sheetName } = useParams();

  // const initialTableData = Array.from({ length: 50 }, () => Array(26).fill(''));
  const [tableData, setTableData] = useState([]);
  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await fetch(`/table/${sheetId}/${sheetName}`);
        const jsonData = await response.json();
        console.log(jsonData);
        const matrixData = convertJSONToMatrix(jsonData);
        //ExtractColumnTypes(jsonData);
        console.log(matrixData);
        setTableData(matrixData);
      } catch (error) {
        console.error('Erreur lors de la récupération des données : ', error);
      }
    };

    fetchData();
  }, [sheetId, sheetName]); // Mettre à jour les données lorsque l'ID de la feuille change

  const convertJSONToMatrix = (jsonData) => {
    if (!jsonData || jsonData.length === 0) {
      return [[]];
    }
  
    const columns = Object.keys(jsonData[0]);
    const matrix = [columns.map(column => [column, jsonData[0][column].type])];
  
    jsonData.forEach((rowData) => {
      const row = columns.map((column) => {
        return rowData[column] ? rowData[column].value : '';
      });
      matrix.push(row);
    });
  
    return matrix;
  };

  const handleCellChange = (rowIndex, colIndex, value) => {
    const updatedTableData = [...tableData];
    updatedTableData[rowIndex][colIndex] = value;
    setTableData(updatedTableData);
  };

  const addColumn = () => {
    const updatedTableData = tableData.map((row) => [...row, '']);
    setTableData(updatedTableData);
  };

  const addRow = () => {
    const newRow = Array(tableData[0].length).fill('');
    setTableData([...tableData, newRow]);
  };
  return (
    <div className="container-fluid p-0 ">
      <header className="p-3 bg-purple text-white d-flex align-items-center">
        <a className="nav-link text-white" href="/">
          <img src="/wexcel_white_on_black_soft.PNG" alt="Logo" height="50" className="mr-3" />
        </a>
        <h1 className="">Feuille {sheetId}</h1>
      </header>
      <div className="container ms-0 p-0">
        <table className="table table-striped">
          <thead>
            <tr>
              <th>ID</th>
              {tableData[0]?.map((column, index) => ( // ? = si tableData[0] existe
                <th className="text-center" key={index}>
                  <input className="text-center"
                    type="text"
                    value={column[0]}
                    onChange={(e) => handleCellChange(0, index, e.target.value)}
                  />
                  <span className="type">{column[1]}</span>
                </th>
              ))}
              <th>
                <button className="btn btn-light bg-purple" onClick={addColumn}>+</button>
              </th>
            </tr>
          </thead>
          <tbody>
            {tableData.slice(1)?.map((row, rowIndex) => (
              <tr key={rowIndex}>
                <th scope="row">{rowIndex + 1}</th>
                {row?.map((cellValue, colIndex) => (
                  <td className="text-center" key={colIndex}>
                    <input
                      type="text"
                      value={cellValue}
                      onChange={(e) => handleCellChange(rowIndex + 1, colIndex, e.target.value)}
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
