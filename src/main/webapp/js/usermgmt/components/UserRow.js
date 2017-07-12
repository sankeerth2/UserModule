/**
 * Created by sanemdeepak on 11/7/16.
 */
import React from "react";

var UserRow = React.createClass({

    render(){
        return (
            <tr id='abcd'>
                <td>sample user</td>
                <td>sample@email.me</td>
                <td>1900001010101</td>
                <td>
                    <div class="squaredTwo">
                        <input type="checkbox" value="None" id="squaredTwo" name="check" checked/>
                        <label for="squaredTwo">
                        </label>
                    </div>
                </td>
                <td>
                    <div class="squaredTwo">
                        <input type="checkbox" value="None" id="squaredTwo" name="check" checked/>
                        <label for="squaredTwo">
                        </label>
                    </div>
                </td>
            </tr>

        );
    }

});

