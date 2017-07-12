/**
 * Created by sanemdeepak on 12/04/16.
 */
import React from "react";
import cookie from "react-cookie";
import LoadingSpinner from "./LoadingSpinner";
require('./../css/style.css');
const client = require("../../client");
let base64 = require('base-64');
let ReactRedirect = require("react-redirect");

export default class Layout extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            invitation: '',
            usernameError: 'hidden',
            passwordError: 'hidden',
            username: '',
            password: '',
            showError: false,
            isAuthenticated: false,
            loading: false

        };

        this.handleUsername = this.handleUsername.bind(this);
        this.handlePassword = this.handlePassword.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.getParameterByName = this.getParameterByName.bind(this);
        this.validateForm = this.validateForm.bind(this);
    }

    handleUsername(event) {
        this.setState({username: event.target.value});
    }

    handlePassword(event) {
        this.setState({password: event.target.value});
    }

    componentWillMount() {
        this.setState({invitation: this.getParameterByName("invitation")});
    }

    componentDidMount() {
        if (!this.state.invitation) {

        }
    }

    validateForm(event) {
        event.preventDefault();

        if (!this.state.username) {
            this.setState({usernameError: 'visible'});
            return;
        }
        if (!this.state.password) {
            this.setState({passwordError: 'visible'});
            return;
        }
        this.handleSubmit(event);
    }

    handleSubmit(event) {
        this.setState({loading: true});
        let requestBody = {
            username: this.state.username,
            password: this.state.password
        };
        event.preventDefault();
        this.setState({loading: true});
        let request = new Request('/api/signup/' + this.state.invitation, {
            method: 'post',
            body: JSON.stringify(requestBody),
            headers: new Headers({
                'Content-Type': 'application/json',
            })
        });

        fetch(request).then(function (responseObj) {
            if (responseObj.status === 200) {
                return responseObj.json();
            }
            else {
                return null;
            }
        }.bind(this)).then(function (data) {
            if (data) {
                cookie.save('efsrToken', "EFSR " + data.token, {path: '/'});
                this.setState({
                    isAuthenticated: true,
                });
            }
            else {
                this.setState({
                    isAuthenticated: false,
                    loading: false
                });
            }
        }.bind(this)).catch(function (err) {
            console.error(err.message);
        });
    }

    render() {
        if (this.state.showError) {
            return (<ReactRedirect location="login"/>);
        }
        if (!this.state.invitation) {
            return (<ReactRedirect location="login"/>);
        }
        if (this.state.isAuthenticated) {
            return (<ReactRedirect location="login"/>);
        }
        else {
            return (
                <div class="form">
                    <form class="login">
                        <h1>Signup with your details</h1>
                        <div class="field-wrap">
                            <input id="username" name="username" type="text"
                                   value={this.state.username}
                                   onChange={this.handleUsername}
                                   required/>
                            <label for="username">Username</label>
                        </div>
                        <p class="user-name-error" style={{visibility: this.state.usernameError, marginBottom: 30}}>
                            <a href="#">Enter Valid Username</a>
                        </p>
                        <div class="field-wrap">
                            <input id="password" name="password" type="password"
                                   value={this.state.password}
                                   onChange={this.handlePassword}
                                   required/>
                            <label for="password">Password</label>
                        </div>
                        <p class="password-error" style={{visibility: this.state.passwordError, marginBottom: 30}}>
                            <a href="#">Enter Valid Password</a>
                        </p>
                        {this.state.loading ?
                            <LoadingSpinner/>
                            :
                            <button class="button button-block"
                                    onClick={this.validateForm}>Sign up
                            </button>
                        }
                    </form>
                </div>

            );
        }
    }

    //helper

    getParameterByName(name) {

        let url = window.location.href;
        name = name.replace(/[\[\]]/g, "\\$&");
        var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
            results = regex.exec(url);
        if (!results) return null;
        if (!results[2]) return '';
        return decodeURIComponent(results[2].replace(/\+/g, " "));
    }
}

