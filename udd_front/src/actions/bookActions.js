import axios from '../api/axios';
import history from '../history';

export const createBook = (data) => async (dispatch) => {
	axios
		.post('/api/prijava/', data)
		.then((response) => {
			alert('Грешка');
		})
		.catch((error) => {
			alert('Направљено');
			history.push('/');
			
		});
};

export const searchBook = (list) => async (dispatch) => {
	dispatch(clearBooks());
	axios.post('/api/prijava/sear', list)
	.then((response) => {
		dispatch({
			type: 'SEARCH_BOOK',
			payload: response.data,
		});
	})
	.catch((error) => {
		history.push('/');
		
	});
};

export const clearBooks = () => ({
	type: 'CLEAR_BOOKS',
});

export const searchPlagiarism = (data) => async (dispatch) => {
	dispatch(clearBooks());

	axios
		.post('/api/book/check', data)
		.then((response) => {
			dispatch({
				type: 'SEARCH_BOOK',
				payload: response.data,
			});
		})
		.catch((error) => {
			console.log(error);
		});
};
