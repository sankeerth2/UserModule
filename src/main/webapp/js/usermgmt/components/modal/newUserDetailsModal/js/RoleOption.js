/**
 * Created by dsanem on 11/29/16.
 */
import React from "react";
import "bootstrap/dist/css/bootstrap.css";
import "rc-dialog/assets/bootstrap.css";
require('./../css/style.css');
var RoleOption = React.createClass({
    render() {
        return (
            <div id='role'>
                <div class="field-wrap-left">
                    <div class="radio">
                        <label><input id={this.props.role} type="radio" name="optradio"
                                      onChange={this.props.handler}/>{this.props.optionLabel}
                        </label>
                    </div>
                </div>
            </div>
        );
    }

});

export default RoleOption;