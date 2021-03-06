import React from 'react';
import FormStore from '../stores/FormStore';
import AuthStore from '../stores/AuthStore';
import ConfigStore from '../stores/ConfigStore';
import FormActionCreators from '../actions/FormActionCreators';
import AuthActionCreators from '../actions/AuthActionCreators';
import ConfigActionCreators from '../actions/ConfigActionCreators';
import SchemaForm from 'react-schema-form/lib/SchemaForm';
import RaisedButton from 'material-ui/lib/raised-button';
import CircularProgress from 'material-ui/lib/circular-progress';
import utils from 'react-schema-form/lib/utils';
import GoogleLogin from 'react-google-login';
import FacebookLogin from 'react-facebook-login';

const id = 'com.networknt.light.user.signin';
const googleConfigId = 'google';
const facebookConfigId = "facebook";

let Login = React.createClass({

    contextTypes: {
        router: React.PropTypes.object.isRequired
    },

    getInitialState: function() {
        return {
            schema: null,
            form: null,
            action: null,
            googleId: null,
            facebookId: null,
            user: {}
        };
    },

    componentDidMount: function() {
        FormStore.addChangeListener(this._onFormChange);
        AuthStore.addChangeListener(this._onAuthChange);
        ConfigStore.addChangeListener(this._onConfigStoreChange);
        ConfigActionCreators.getConfig(googleConfigId);
        ConfigActionCreators.getConfig(facebookConfigId);
        FormActionCreators.getForm(id);
    },

    componentWillUnmount: function() {
        FormStore.removeChangeListener(this._onFormChange);
        AuthStore.removeChangeListener(this._onAuthChange);
        ConfigStore.removeChangeListener(this._onConfigStoreChange);
    },

    _onModelChange: function(key, val) {
        utils.selectOrSet(key, this.state.user, val);
        //this.forceUpdate();
    },


    _onFormChange: function() {
        let schema = FormStore.getForm(id) ? FormStore.getForm(id).schema : null;
        if(schema) {
            let form = FormStore.getForm(id).form;
            let action = FormStore.getForm(id).action;
            this.setState({
                schema: schema,
                form: form,
                action: action
            });
        }
    },

    _onAuthChange: function() {
        // route to user page once it is logged in.
        if(AuthStore.isLoggedIn()) {
            this.context.router.push('profile');
        }
    },

    _onConfigStoreChange: function() {
        this.setState({
            googleId: ConfigStore.getConfig(googleConfigId).client_id,
            facebookId: ConfigStore.getConfig(facebookConfigId).appId
        });
    },

    _responseGoogle: function(response) {
        console.log('responseGoogle.AuthResponse', response.getAuthResponse());
        AuthActionCreators.googleLogin(response.getAuthResponse());
    },

    _responseFacebook: function(response) {
        console.log('responseFacebook', response);
        AuthActionCreators.facebookLogin(response);
    },

    render: function() {
        console.log('Login._onModelChange', this.state.user);
        let socialLogin = '';
        if(this.state.googleId && this.state.facebookId) {
            socialLogin = (
                <div className="socialButton">
                    <GoogleLogin
                        clientId={this.state.googleId}
                        callback={this._responseGoogle}
                        cssClass="googleLogin"
                        offline={false}
                    />
                    <FacebookLogin
                        appId={this.state.facebookId}
                        autoLoad={false}
                        scope="public_profile, email"
                        cssClass="facebookLogin"
                        callback={this._responseFacebook}
                    />
                </div>
            )
        }
        if(this.state.schema) {
            const buttons = this.state.action.map((item, idx) => (
                <RaisedButton key={idx} label={item.title} primary={true}
                    onTouchTap = {(e) => (AuthActionCreators.login(this.state.user.userIdEmail, this.state.user.password, this.state.user.rememberMe))} />
            ));

            return (

                <div>
                    {socialLogin}
                    <SchemaForm schema={this.state.schema} form={this.state.form} model={this.state.user} onModelChange={this._onModelChange} />
                    {buttons}
                </div>
            )
        } else {
            return (<CircularProgress mode="indeterminate"/>);
        }
    }
});

module.exports = Login;
