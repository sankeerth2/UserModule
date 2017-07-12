/**
 * Created by sanemdeepak on 11/29/16.
 */
import React from "react";
import "bootstrap/dist/css/bootstrap.css";
import "rc-dialog/assets/bootstrap.css";
require('./../css/style.css');
var PermissionCheckbox = React.createClass({
    render() {
        return (
            <div id='permission'>
                <div class="field-wrap-left">
                    <div class="checkbox">
                        <label><input id={this.props.permission} type="checkbox"
                                      name="optchkbox"
                                      onChange={this.props.handler}
                                      checked={this.props.checked}/>{this.props.optionLabel}
                        </label>
                    </div>
                </div>
            </div>
        );
    }
});

export default PermissionCheckbox;