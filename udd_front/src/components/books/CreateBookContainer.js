import React, { useState } from 'react';
import { connect } from 'react-redux';
import { Field, reduxForm } from 'redux-form';
import { Divider, Header, Segment } from 'semantic-ui-react';
import { createBook } from '../../actions/bookActions';
import InputField from '../InputField';
import Map from '../Map';

const regExpName = RegExp('^[a-zA-Z][a-zA-Z ]{1,100}$');
const regIsbn = RegExp('^[0-9]{13}$');

const CreateBookContainer = (props) => {
	const [ file, setFile ] = useState(null);
	const [ showFileError, setShowFileError ] = useState(false);


	const onSubmit = (formValues) => {
		if (file == null) {
			setShowFileError(true);
			return;
		}
		const formData = new FormData();

		formData.append('file', file);
		formData.append('ime', formValues.ime);
		formData.append('prezime', formValues.prezime);
		formData.append('email', formValues.email);
		formData.append('adresa', formValues.adresa);
		formData.append('stepenObrazovanja', formValues.stepenObrazovanja);

		console.log(formData)
		props.createBook(formData);
	};

	const handleUpload = (e) => {
		setFile(e.target.files[0]);
		setShowFileError(false);
	};

	return (
		<div className='ui container'>
			<Segment>
				<form onSubmit={props.handleSubmit(onSubmit)} className='ui form error'>
					<Divider horizontal>
						<Header as='h2'>Prijavi se za posao</Header>
					</Divider>
					<div className='three fields'>
						<div className='field'>
							<Segment>
								<Field
									name='ime'
									component={InputField}
									label='Ime'
									type='text'
								/>
							</Segment>
						</div>
						<div className='field'>
							<Segment>
								<Field
									name='prezime'
									component={InputField}
									label='Prezime'
									type='text'
								/>
							</Segment>
						</div>
					</div>
					<div className='two fields'>
						<div className='field'>
							<Segment>
								<Field
									name='email'
									component={InputField}
									label='Email'
									type='text'
								/>
							</Segment>
						</div>
						<div className='field'>
							<Segment>
								<Field
									name='adresa'
									component={InputField}
									label='Adresa'
									type='text'
								/>
							</Segment>
						</div>	
						<div className='field'>
							<Segment>
								<Field
									name='stepenObrazovanja'
									component={InputField}
									label='Stepen obrazovanja'
									type='text'
								/>
							</Segment>
						</div>
					</div>
					<div className='field'>
						<Segment>
							<input
								type='file'
								id='avatar'
								name='avatar'
								accept='application/msword, text/plain, application/pdf'
								onChange={handleUpload}
							/>
							{showFileError ? (
								<div className='ui error message'>
									<div className='header'>
										Morate priloziti CV dokument
									</div>
								</div>
							) : (
								undefined
							)}
						</Segment>
					</div>
					
					<div className='field'>
						<div
							className='button green ui fluid'
							onClick={props.handleSubmit(onSubmit)}
						>
							Dodaj
						</div>
					</div>
				
				</form>
			</Segment>
		</div>
	);
};

const mapStateToProps = (state) => ({});

const mapDispatchToProps = { createBook };

const validate = (values) => {
	const errors = {};

	if (!regExpName.test(values.firstName) || values.firstName == null) {
		errors.firstName = 'Морате унети име';
	}
	if (!regExpName.test(values.lastName) || values.lastName == null) {
		errors.lastName = 'Морате унети презиме';
	}
	if (!regExpName.test(values.title) || values.title == null) {
		errors.title = 'Морате унети наслов';
	}
	if (!regExpName.test(values.genre) || values.genre == null) {
		errors.genre = 'Морате унети жанр';
	}
	if (!regIsbn.test(values.isbn) || values.isbn == null) {
		errors.isbn = 'Морате унети ИСБН';
	}

	return errors;
};
const wrappedForm = reduxForm({ form: 'createBook', validate })(CreateBookContainer);

export default connect(mapStateToProps, mapDispatchToProps)(wrappedForm);
