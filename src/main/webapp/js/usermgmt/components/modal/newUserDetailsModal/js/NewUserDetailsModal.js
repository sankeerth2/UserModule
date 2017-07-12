/**
 * Created by dsanem on 11/28/16.
 */
import React from "react";
import "bootstrap/dist/css/bootstrap.css";
import "rc-dialog/assets/bootstrap.css";
require('./../css/style.css');
var Dialog = require('rc-dialog');


const NewUserDetailsModal = React.createClass({

    getInitialState () {
        return {
            firstName: "",
            lastName: "",
            email: "",
            mobile: "",
            firstNameError: 'hidden',
            lastNameError: 'hidden',
            emailError: 'hidden',
            mobileError: 'hidden',
        };
    },

    handleFirstname(event) {
        this.setState({firstName: event.target.value});
        this.setState({firstNameError: 'hidden'});
    },

    handleLastname(event) {
        this.setState({lastName: event.target.value});
        this.setState({lastNameError: 'hidden'});
    },

    handleMobile(event) {
        if (event.target.value.length <= 10) {
            this.setState({mobile: event.target.value});
        }
        this.setState({mobileError: 'hidden'});
    },

    handleEmail(event) {
        this.setState({email: event.target.value});
        this.setState({emailError: 'hidden'});
    },

    //TODO make this better to show error on all fields which are empty
    validateInputs(event) {
        var emailRegex = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        if (!this.state.firstName) {
            console.log("Empty String for first name");
            this.setState({firstNameError: 'visible'});
            return;
        }
        if (!this.state.lastName) {
            console.log("Empty string for last name");
            this.setState({lastNameError: 'visible'});

            return;
        }
        if (!emailRegex.test(this.state.email)) {
            console.log("empty string for email");
            this.setState({emailError: 'visible'});
            return;
        }

        if (!this.state.mobile) {
            console.log("empty string for mobile");
            this.setState({mobileError: 'visible'});
            return;
        }
        let userData = {
            firstName: this.state.firstName,
            lastName: this.state.lastName,
            email: this.state.email,
            mobile: this.state.mobile
        }
        event.data = userData;
        this.props.handler(event);
    },

    render() {
        let dialog;
        if (this.props.visible) {
            dialog = (
                <Dialog
                    visible={this.props.visible}
                    animation="slide-fade"
                    maskAnimation="fade"
                    onClose={this.props.exitDialogHandler}
                    style={{width: 600}}
                    bodyStyle={{width: 600, height: 300, top: 25}}
                    title={<div><p class="dialog-title">User Details</p></div>}
                    footer={
                        [
                            <div class="dialog-footer">
                                <button
                                    type="button"
                                    className="btn btn-default"
                                    id="close-user-details-btn"
                                    onClick={this.props.handler}
                                >Close
                                </button>
                                <button
                                    type="submit"
                                    className="btn btn-primary"
                                    id="role-permissions-btn"
                                    onClick={this.validateInputs}
                                >
                                    Role and Permissions
                                </button>
                            </div>
                        ]
                    }
                >
                    <form class="form-user-details">
                        <div>
                            <div class="field-wrap-left">
                                <input id="firstName" class="form-control" type="text" placeholder="First name"
                                       required
                                       value={this.state.firstName}
                                       onChange={this.handleFirstname}/>
                                <p class="input-validation-error-message"
                                   style={{visibility: this.state.firstNameError}}>Enter valid value</p>
                            </div>
                            <div class="field-wrap-left">
                                <input id="lastName" class="form-control" placeholder="Last name "
                                       type="text" value={this.state.lastName}
                                       required
                                       onChange={this.handleLastname}/>
                                <p class="input-validation-error-message"
                                   style={{visibility: this.state.lastNameError}}>Enter valid value</p>
                            </div>
                            <div class="field-wrap-left">
                                <input id="mobile" class="form-control" placeholder="Mobile"
                                       type="number"
                                       value={this.state.mobile}
                                       required
                                       onChange={this.handleMobile}/>
                                <p class="input-validation-error-message"
                                   style={{visibility: this.state.mobileError}}>Enter valid value</p>
                            </div>
                            <div class="field-wrap-left">
                                <input id="email" class="form-control" placeholder="Email" type="email"
                                       pattern="[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,4}$"
                                       value={this.state.email}
                                       required
                                       onChange={this.handleEmail}/>
                                <p class="input-validation-error-message"
                                   style={{visibility: this.state.emailError}}>Enter valid value</p>
                            </div>
                        </div>
                    </form>
                </Dialog>
            );
        }
        return (
            <div style={{margin: 20}}>
                {dialog}
            </div>
        );
    },
});

export default NewUserDetailsModal;
