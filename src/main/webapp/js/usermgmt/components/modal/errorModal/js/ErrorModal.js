/**
 * Created by dsanem on 11/28/16.
 */
import React from "react";
import "bootstrap/dist/css/bootstrap.css";
import "rc-dialog/assets/bootstrap.css";
require('./../css/style.css');
var Dialog = require('rc-dialog');


const ErrorModal = React.createClass({

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
                    title={<div><p class="error-title">Error</p></div>}
                    footer={
                        [
                            <button
                                type="button"
                                className="btn btn-default"
                                key="close"
                                onClick={this.props.exitDialogHandler}
                            >
                                Close
                            </button>,
                        ]
                    }
                >
                    <h4>Something went wrong, we will fix it soon</h4>
                    <p>Try refreshing the page</p>
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

export default ErrorModal;
