import * as React from 'react';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';

export default function BasicTable({ data }) {

  if (!data || data.length === 0) {
    return null; // Gestisci il caso in cui i dati sono vuoti o non disponibili
  }

  return (
    <div style={{ margin: '25px', display: 'flex', justifyContent: 'center' }}>
      <TableContainer component={Paper}>
        <Table style={{ tableLayout: 'fixed' }} aria-label="simple table">
          <TableHead>
            <TableRow>
              <TableCell style={{ width: '90%' }} align="left">Note</TableCell>
              <TableCell style={{ width: '10%' }} align="right">Occurrencies</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {data.map((row, index) => (
              <TableRow key={index} className={index < 3 ? 'colored-row' : ''}>
                <TableCell style={{ width: '90%', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }} align="left">{row.key}</TableCell>
                <TableCell style={{ width: '10%' }} align="right">{row.value}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </div>
  );
}