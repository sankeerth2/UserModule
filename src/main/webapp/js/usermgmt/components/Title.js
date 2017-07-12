/**
 * Created by sanemdeepak on 10/29/16.
 */

import React from "react";


export default class Title extends React.Component {
    render() {
        return (
            <h1>{this.props.title}</h1>
        );
    }
}
