require("dotenv-safe").config();

const jwt = require("jsonwebtoken");
var http = require('http');
const express = require('express');
const httpProxy = require("express-http-proxy")
const app = express();
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var logger = require("morgan");
const helmet = require('helmet');

app.use(bodyParser.urlencoded({extended: false}));
app.use(bodyParser.json());

const invalidTokens = new Set(); // Lista de tokens inválidos

const clientesServiceProxy = httpProxy('http://localhost:5000/clientes', {
    proxyReqOptDecorator: function(proxyReqOpts, srcReq) {
        proxyReqOpts.headers['Content-Type'] = 'application/json';
        proxyReqOpts.method = 'POST';
        return proxyReqOpts;
    },


    proxyReqBodyDecorator: function(bodyContent, srcReq) {

        try {

            let retBody = {};
            retBody.email = bodyContent.user;
            retBody.password = bodyContent.password;
            bodyContent = retBody;

        } catch(error) {
            console.log('ERRO: ' + error);
        };

        return bodyContent;
    },


    userResDecorator: function(proxyRes, proxyResData, req, res) {
        const data = JSON.parse(proxyResData.toString('utf8'));
        return JSON.stringify(data);
    }
});

const gerentesServiceProxy = httpProxy('http://localhost:5000/gerentes', {
    proxyReqOptDecorator: function(proxyReqOpts, srcReq) {
        proxyReqOpts.headers['Content-Type'] = 'application/json';
        proxyReqOpts.method = 'POST';
        return proxyReqOpts;
    },

    proxyReqBodyDecorator: function(bodyContent, srcReq) {

        try {
            
            let retBody = {};
            retBody.email = bodyContent.user;
            retBody.password = bodyContent.password;
            bodyContent = retBody;

        } catch(error) {
            console.log('ERRO: ' + error);
        };

        return bodyContent;
    },

    userResDecorator: function(proxyRes, proxyResData, req, res) {
        const data = JSON.parse(proxyResData.toString('utf8'));
        return JSON.stringify(data);
    }
});

const contaServiceProxy = httpProxy("http://localhost:5000/contas", {
    proxyReqOptDecorator: function(proxyReqOpts, srcReq) {
        proxyReqOpts.headers['Content-Type'] = 'application/json';
        proxyReqOpts.method = 'POST';
        return proxyReqOpts;
    },

    proxyReqBodyDecorator: function(bodyContent, srcReq) {
        
        try {
            let retBody = {};
            retBody.email = bodyContent.user;
            retBody.password = bodyContent.password;
            bodyContent = retBody;

        } catch(error) {
            console.log('ERRO: ' + error);
        }
        return bodyContent;
    },

    userResDecorator: function(proxyRes, proxyResData, req, res) {
        const data = JSON.parse(proxyResData.toString('utf8'));
        return JSON.stringify(data);
    }
});

const authServiceProxy = httpProxy("http://localhost:8080/auth", {
    proxyReqOptDecorator: function(proxyReqOpts, srcReq) {
        proxyReqOpts.headers['Content-Type'] = 'application/json';
        proxyReqOpts.method = 'POST';
        return proxyReqOpts;
    },

    proxyReqBodyDecorator: function(bodyContent, srcReq) {
        try {
            let retBody = {};
            retBody.email = bodyContent.email;
            retBody.senha = bodyContent.password;
            console.log(bodyContent)
            console.log(retBody);
            bodyContent = retBody;
        } catch(error) {
            console.log('ERRO: ' + error);
        }
        return bodyContent;
    },


    userResDecorator: function(proxyRes, proxyResData, req, res) {
        if (proxyRes.statusCode === 200) {
            const str = Buffer.from(proxyResData).toString('utf-8');
            const objBody = JSON.parse(str);
            const email = objBody.email;
            const token = jwt.sign({email}, process.env.SECRET, {
                expiresIn: 30000
            });
            res.status(200);
            return {auth: true, token: token, data: objBody};
        } else {
            res.status(401);
            return {message: 'Login inválido!'};
        }
    }
});

const verifyJWT = (req, res, next) => {
    const token = req.headers['x-access-token'];
    if(!token) {
        return res.status(401).json({auth: false, message: "Token não fornecido"});
    }
    if (invalidTokens.has(token)) {
        return res.status(401).json({auth: false, message: "Token inválido"});
    }
    jwt.verify(token, process.env.SECRET, (err, decoded) => {
        if (err) {
            return res.status(500).json({auth: false, message: 'Falha ao autenticar o token'});
        }
        req.userId = decoded.id;
        next();
    });
};

app.post('/auth', (req, res, next) => {
    authServiceProxy(req, res, next);
});


app.post('/logout', (req, res) => {
    const token = req.headers['x-access-token'];
    if (token) {
        invalidTokens.add(token); // Adiciona o token à lista de tokens inválidos
        res.status(200).json({auth: false, message: 'Logout bem-sucedido!'});
    } else {
        res.status(400).json({message: 'Token não fornecido'});
    }
});


app.get('/clientes', verifyJWT, (req, res, next) => {
    clientesServiceProxy(req, res, next);
});


app.get('/conta', verifyJWT, (req, res, next) => {
    contaServiceProxy(req, res, next);
});


app.get('/gerentes', verifyJWT, (req, res, next) => {
    gerentesServiceProxy(req, res, next);
});

app.use(logger('dev'));
app.use(helmet());
app.use(express.json());
app.use(express.urlencoded({extended: false}));
app.use(cookieParser());

var server = http.createServer(app);
server.listen(3000);
