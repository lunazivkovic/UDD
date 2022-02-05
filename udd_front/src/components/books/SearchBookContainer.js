import React, { useEffect, useState } from 'react';
import { connect } from 'react-redux';
import { Card, Checkbox, Divider, Grid, Header, Segment } from 'semantic-ui-react';
import { clearBooks, searchBook } from '../../actions/bookActions';
import { OneBook } from './OneBook';
import './SearchBookContainer.css';
const SearchCV = (props) => {
	const [ ime, setIme ] = useState('');
	const [ Prezime, setPrezime ] = useState('');
	const [ textContent, setTextContent ] = useState('');
	const [ StepenObrazovanja, setStepenObrazovanja ] = useState('');

	const [ phraseIme, setPhraseIme ] = useState(false);
	const [ phrasePrezime, setPhrasePrezime ] = useState(false);
	const [ phraseStepenObrazovanja, setPhraseStepenObrazovanja ] = useState(false);
	const [ phraseTextContent, setPhraseTextContent ] = useState(false);

	const [ mustIme, setMustIme ] = useState(false);
	const [ mustPrezime, setMustPrezime ] = useState(false);
	const [ mustStepenObrazovanja, setMustStepenObrazovanja ] = useState(false);
	const [ mustTextContent, setMustTextContent ] = useState(false);

	useEffect(() => {
		return () => {
			props.clearBooks();
		};
	}, []);

	const onSubmit = (e) => {
		e.preventDefault();

		const list = [];

	
		if (ime.length != 0) {
			const data = {};
			data.field = 'ime';
			data.value = ime;
			data.phraseQuery = phraseIme;
			data.logic = mustIme ? 'AND' : 'OR';

			list.push(data);
		}
		if (Prezime.length != 0) {
			const data = {};
			data.field = 'Prezime';
			data.value = Prezime;
			data.phraseQuery = phrasePrezime;
			data.logic = mustPrezime ? 'AND' : 'OR';

			list.push(data);
		}
		if (StepenObrazovanja.length != 0) {
			const data = {};
			data.field = 'StepenObrazovanja';
			data.value = StepenObrazovanja;
			data.phraseQuery = phraseStepenObrazovanja;
			data.logic = mustStepenObrazovanja ? 'AND' : 'OR';

			list.push(data);
		}
		if (textContent.length != 0) {
			const data = {};
			data.field = 'textContent';
			data.value = textContent;
			data.phraseQuery = phraseTextContent;
			data.logic = mustTextContent ? 'AND' : 'OR';

			list.push(data);
		}

		console.table(list);

		props.searchBook(list);
	};

	return (
		<div className='ui container'>
			<Divider horizontal>
				<Header as='h2'>Pretrazi CV-eve</Header>
			</Divider>
			<Grid columns={2}>
				<Grid.Column>
					<Segment>
						<form onSubmit={onSubmit} className='ui form error'>
						
							<div className='three fields'>
								<div className='field'>
									<label>Име</label>
									<input
										type='text'
										name='ime'
										id='title'
										placeholder='Име'
										value={ime}
										onChange={(e) =>
											setIme(
												e.target.value
											)}
									/>
								</div>
								<div className='field'>
									<label>Фраза</label>
									<Checkbox
										toggle
										checked={phraseIme}
										onChange={() =>
											setPhraseIme(
												!phraseIme
											)}
									/>
								</div>
								<div className='field'>
									<label>ИЛИ/И</label>
									<Checkbox
										toggle
										checked={mustIme}
										onChange={() =>
											setMustIme(
												!mustIme
											)}
									/>
								</div>
							</div>

							<div className='three fields'>
								<div className='field'>
									<label>Prezime</label>
									<input
										type='text'
										name='prezime'
										id='title'
										placeholder='Prezime'
										value={Prezime}
										onChange={(e) =>
											setPrezime(
												e.target.value
											)}
									/>
								</div>
								<div className='field'>
									<label>Фраза</label>
									<Checkbox
										toggle
										checked={phrasePrezime}
										onChange={() =>
											setPhrasePrezime(
												!phrasePrezime
											)}
									/>
								</div>
								<div className='field'>
									<label>ИЛИ/И</label>
									<Checkbox
										toggle
										checked={mustPrezime}
										onChange={() =>
											setMustPrezime(!mustPrezime)}
									/>
								</div>
							</div>
							<div className='three fields'>
								<div className='field'>
									<label>Stepen obrazovanja</label>
									<input
										type='text'
										name='StepenObrazovanja'
										id='title'
										placeholder='Stepen obrazovanja'
										value={StepenObrazovanja}
										onChange={(e) =>
											setStepenObrazovanja(e.target.value)}
									/>
								</div>
								<div className='field'>
									<label>Фраза</label>
									<Checkbox
										toggle
										checked={phraseStepenObrazovanja}
										onChange={() =>
											setPhraseStepenObrazovanja(!phraseStepenObrazovanja)}
									/>
								</div>
								<div className='field'>
									<label>ИЛИ/И</label>
									<Checkbox
										toggle
										checked={mustStepenObrazovanja}
										onChange={() =>
											setMustStepenObrazovanja(!mustStepenObrazovanja)}
									/>
								</div>
							</div>
							<div className='three fields'>
								<div className='field'>
									<label>Sadrzaj</label>
									<textarea
										id=''
										placeholder='Sadrzaj'
										value={textContent}
										onChange={(e) =>
											setTextContent(e.target.value)}
									/>
								</div>
								<div className='field'>
									<label>Фраза</label>
									<Checkbox
										toggle
										checked={phraseTextContent}
										onChange={() =>
											setPhraseTextContent(
												!phraseTextContent
											)}
									/>
								</div>
								<div className='field'>
									<label>ИЛИ/И</label>
									<Checkbox
										toggle
										checked={mustTextContent}
										onChange={() =>
											setMustTextContent(
												!mustTextContent
											)}
									/>
								</div>
							</div>
							<div className='field'>
								<div
									className='button green ui fluid'
									onClick={onSubmit}
								>
									Pretrazi
								</div>
							</div>
						</form>
					</Segment>
				</Grid.Column>
				<Grid.Column>
					<Segment>
						<Card.Group itemsPerRow={2}>
							{props.books.map((i) => {
								return (
									<OneBook
										key={
											i.textContent +
											i.id +
											i.ime
										}
										ime={i.ime}
										Prezime={i.prezime}
										adresa={i.adresa}
										email={i.email}
										textContent={i.textContent}
										url={i.url}
										StepenObrazovanja={i.stepenObrazovanja}
									/>
								);
							})}
						</Card.Group>
					</Segment>
				</Grid.Column>
			</Grid>
		</div>
	);
};

const mapStateToProps = (state) => ({
	books: state.books.books,
});

const mapDispatchToProps = { searchBook, clearBooks };

export default connect(mapStateToProps, mapDispatchToProps)(SearchCV);
