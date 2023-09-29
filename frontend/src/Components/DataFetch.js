import React, { useState, useEffect } from 'react';
import BasicTable from './BasicTable';

function DataFetchingComponent() {
  const [data, setData] = useState([]);

  useEffect(() => {
    fetch('http://localhost:8080/ciao')
      .then((response) => response.json())
      .then((data) => {
        const mappedData = data.map((mapItem) => {
            // Estrai la chiave e il valore dalla mappa
        const key = Object.keys(mapItem)[0];
        const value = mapItem[key];

            // Restituisci un oggetto JavaScript con chiave e valore
            return { key, value };
          });
        setData(mappedData); // Aggiorna lo stato con i dati ricevuti
      })
      .catch((error) => {
        console.error('Errore nella richiesta:', error);
      });
  }, []);

  return (
    <BasicTable data={data} /> // Passa i dati al componente BasicTable come props
  );
}

export default DataFetchingComponent;
