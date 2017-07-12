/**
 * Created by sanemdeepak on 10/29/16.
 */
import React from "react";
import LoadingSpinner from "./LoadingSpinner";
import Loading from "react-loading-spinner";
import cookie from "react-cookie";
require('./../css/style.css');
require('./../scss/style.scss');
const client = require("../../client");
let base64 = require('base-64');
let ReactRedirect = require("react-redirect");

export default class Layout extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            username: '',
            password: '',
            token: cookie.load('efsrToken'),
            isAuthenticated: false,
            loading: false,
            loadPage: false
        };

        this.handleUsername = this.handleUsername.bind(this);
        this.handlePassword = this.handlePassword.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }


    componentDidMount() {
        let request = new Request('/api/secured/ping', {
            method: 'get',
            headers: new Headers({
                'Content-Type': 'application/json',
                'Authorization': this.state.token
            })
        });

        fetch(request).then(function (responseObj) {
            if (responseObj.status === 200) {
                this.setState({
                    isAuthenticated: true,
                    loadPage: false
                });
            }
            else {
                this.setState({
                    isAuthenticated: false,
                    loadPage: true
                });
            }
        }.bind(this)).catch(function (err) {
            console.error(err.message);
        });
    }

    handleUsername(event) {
        this.setState({username: event.target.value});
    }

    handlePassword(event) {
        this.setState({password: event.target.value});
    }

    handleSubmit(event) {
        event.preventDefault();
        this.setState({loading: true});
        let encodedData = 'Basic ' + base64.encode(this.state.username + ':' + this.state.password);
        let request = new Request('/api/token', {
            method: 'post',
            headers: new Headers({
                'Content-Type': 'application/json',
                'Authorization': encodedData
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
                    loading: false,
                    loadPage: false
                });
            }
            else {
                this.setState({
                    isAuthenticated: false,
                    loadPage: true,
                    loading: false
                });
            }
        }.bind(this)).catch(function (err) {
            console.error(err.message);
        });
    }

    render() {
        return (
            //TODO: improve this crappy thing, renders whole page then redirects which sucks ¯\_(ツ)_/¯
            this.state.isAuthenticated ? <ReactRedirect location='users'/> :
                this.state.loadPage ?
                    <div class="form">
                        <form class="login">
                            <h1>Login to EFSR!</h1>
                            <div class="field-wrap">
                                <input
                                    type="text"
                                    required autoComplete="off"
                                    id="username"
                                    name="username"
                                    value={this.state.username}
                                    onChange={this.handleUsername}/>
                                <label for="username">Username</label>
                            </div>
                            <div class="field-wrap">
                                <input
                                    type="password"
                                    required autoComplete="off"
                                    id="password"
                                    name="password"
                                    value={this.state.password}
                                    onChange={this.handlePassword}/>
                                <label for="password">Password</label>
                            </div>
                            <p class="forgot"><a href="#">Forgot Password?</a></p>
                            {this.state.loading ?
                                <Loading isLoading={this.state.loading}
                                         spinner={LoadingSpinner}/>
                                :
                                <button class="button button-block"
                                        onClick={this.handleSubmit}>Log In
                                </button>
                            }
                        </form>
                    </div>
                    :
                    <div class="form">
                        <form class="login">
                            <Loading isLoading={true}
                                     spinner={LoadingSpinner}/>
                        </form>
                    </div>
        );
    }
}

