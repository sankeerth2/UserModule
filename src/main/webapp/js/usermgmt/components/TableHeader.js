/**
 * Created by sanemdeepak on 11/7/16.
 */

import React from "react";

export default class TableHeader extends React.Component {

    render() {
        return (
            <thead>
            <tr>
                <th>
                    <h1>Full Name</h1></th>
                <th>
                    <h1>Email</h1></th>
                <th>
                    <h1>Mobile</h1></th>
                <th>
                    <h1>Active</h1></th>
                <th>
                    <h1>Delete</h1></th>
            </tr>
            </thead>
        );
    }
}
