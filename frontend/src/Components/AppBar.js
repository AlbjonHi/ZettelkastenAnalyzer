import * as React from 'react';
import { styled, alpha } from '@mui/material/styles';
import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import InputBase from '@mui/material/InputBase';
import SearchIcon from '@mui/icons-material/Search';
import { useState } from 'react';
import BasicTable from './BasicTable';

const Search = styled('div')(({ theme }) => ({
  position: 'relative',
  borderRadius: theme.shape.borderRadius,
  backgroundColor: alpha(theme.palette.common.white, 0.15),
  '&:hover': {
    backgroundColor: alpha(theme.palette.common.white, 0.25),
  },
  marginRight: theme.spacing(0),
  marginLeft: 0,
  width: '100%',
}));

// Icona ricerca
const SearchIconWrapper = styled('div')(({ theme }) => ({
  padding: theme.spacing(0, 2),
  height: '100%',
  position: 'absolute',
  pointerEvents: 'none',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
}));

const StyledInputBase = styled(InputBase)(({ theme }) => ({
  color: 'inherit',
  '& .MuiInputBase-input': {
    padding: theme.spacing(1, 1, 1, 0),
    paddingLeft: `calc(1em + ${theme.spacing(4)})`,
    transition: theme.transitions.create('width'),
    width: '100%',
    [theme.breakpoints.up('md')]: {
      width: '90ch',
    },
  },
}));

export default function PrimarySearchAppBar() {

  const [searchTerm, setSearchTerm] = useState('');
  const [tableData, setTableData] = useState([]); // State to hold table data
  const [debounceTimeout, setDebounceTimeout] = useState(null);

  const performSearch = (searchTerm) => {
    // Imposta l'URL del tuo endpoint di ricerca, sostituendo 'your-api-endpoint' con l'effettivo URL del tuo backend.
    const apiUrl = `http://localhost:8080/search?words=${searchTerm}`;
  
    // Esegui la richiesta GET solo se il termine di ricerca non è vuoto.
    if (searchTerm !== '') {
      fetch(apiUrl)
        .then((response) => response.json())
        .then((data) => {
          // Qui puoi gestire la risposta dalla tua API e aggiornare lo stato con i risultati della ricerca.
          console.log(data);
          
          const mappedData = data.map((mapItem) => {
            const key = Object.keys(mapItem)[0];
            const value = mapItem[key];
            return { key, value };
          });
  
          setTableData(mappedData);
        })
        .catch((error) => {
          console.error(error);
        });
    }
  };

  const handleSearchChange = (event) => {
    const newSearchTerm = event.target.value;
    setSearchTerm(newSearchTerm);
  
    // Cancella il timeout precedente se esiste
    if (debounceTimeout) {
      clearTimeout(debounceTimeout);
    }
  
    // Imposta un nuovo timeout per 500ms
    const newDebounceTimeout = setTimeout(() => {
      // Esegui la ricerca dopo il timeout
      performSearch(newSearchTerm);
    }, 500);
  
    // Salva l'ID del timeout nel nuovo stato
    setDebounceTimeout(newDebounceTimeout);
  };

  return (
    <Box sx={{ flexGrow: 1 }}>
      <AppBar position="static">
        <Toolbar>
          <Search>
            <SearchIconWrapper>
              <SearchIcon />
            </SearchIconWrapper>
            <StyledInputBase
              placeholder="Search…"
              inputProps={{ 'aria-label': 'search' }}
              onChange={handleSearchChange}
            />
          </Search>
          <Box sx={{ flexGrow: 1 }} />
          <Box sx={{ display: { xs: 'none', md: 'flex' } }}>
          </Box>
          <Box sx={{ display: { xs: 'flex', md: 'none' } }}>
          </Box>
        </Toolbar>
      </AppBar>
      <BasicTable data={tableData} />
    </Box>
  );
}
