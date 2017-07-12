// /**
//  * Created by sanemdeepak on 10/29/16.
//  */
//
// import React from "react";
// import Title from "./Title";
// const client = require("../../client");
// var base64 = require('base-64');
//
// export default class Header extends React.Component {
//
//     constructor(props) {
//         super(props);
//         this.state = {
//             username: '',
//             password: ''
//         };
//         this.handleUsername = this.handleUsername.bind(this);
//         this.handlePassword = this.handlePassword.bind(this);
//         this.handleSubmit = this.handleSubmit.bind(this);
//     }
//
//     handleUsername(event) {
//         this.setState({username: event.target.value});
//     }
//
//     handlePassword(event) {
//         this.setState({password: event.target.value});
//     }
//
//     handleSubmit(event) {
//         var encodedData = 'Basic ' + base64.encode(this.state.username + ':' + this.state.password);
//         console.log(encodedData);
//         var request = new Request('/api/test/sec/', {
//             method: 'get',
//             headers: new Headers({
//                 'Authorization': encodedData
//             })
//         });
//
//         fetch(request).then(function (responseobj) {
//             this.props.changeTitle(responseobj.status);
//             console.log(responseobj.status)
//
//         }.bind(this)).catch(function (err) {
//             // Error :(
//         });
//         //alert('Username: ' + this.state.username + 'Password is:' + this.state.password);
//         //alert(this.props.title);
//     }
//
//     handleClick = () => {
//         var status
//         fetch('http://localhost:8080/api/test/sec', {
//             method: 'GET'
//         }).then(function (responseobj) {
//             this.props.changeTitle(responseobj.status);
//             console.log(responseobj.status)
//
//         }.bind(this)).catch(function (err) {
//             // Error :(
//         });
//
//     }
//
//     render() {
//         return (
//             <div>
//                 <Title title={this.props.title}/>
//                 <input id="username"
//                        name="username"
//                        value={this.state.username}
//                        onChange={this.handleUsername}/>
//                 <div>
//                     <input id="password"
//                            name="password"
//                            value={this.state.password}
//                            onChange={this.handlePassword}/>
//                 </div>
//                 <div>
//                     <button onClick={this.handleSubmit}>login</button>
//                 </div>
//             </div>
//         );
//     }
// }
//
