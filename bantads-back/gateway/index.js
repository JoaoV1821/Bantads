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
const cors = require('cors');

app.use(bodyParser.urlencoded({extended: false}));
app.use(bodyParser.json());
app.use(logger('dev'));
app.use(helmet());
app.use(express.json());
app.use(express.urlencoded({extended: false}));
app.use(cookieParser());
app.use(cors());

app.use(cors({
    origin: 'http://localhost:4200',
    methods: ['GET', 'POST', 'PUT', 'DELETE'],
    allowedHeaders: ['Content-Type']
  }));

const invalidTokens = new Set(); // Lista de tokens inválidos

const sagaAutocadastroProxy = httpProxy("http://localhost:8084/autocadastro", {
    proxyReqOptDecorator: function(proxyReqOpts, srcReq) {
        proxyReqOpts.headers['Content-Type'] = 'application/json';
        proxyReqOpts.method = 'POST';
        return proxyReqOpts;
    },
    proxyReqBodyDecorator: function(bodyContent, srcReq) {
        
        try {

            let retBody = {};

            retBody.cpf = bodyContent.cpf;
            retBody.email = bodyContent.email;
            retBody.nome = bodyContent.nome;
            retBody.salario = bodyContent.salario;
            retBody.telefone = bodyContent.telefone;
            
            retBody.logradouro = bodyContent.logradouro;
            retBody.numero = bodyContent.numero;
            retBody.complemento = bodyContent.complemento;
            retBody.cep = bodyContent.cep;
            retBody.cidade = bodyContent.cidade;
            retBody.uf = bodyContent.uf;

            bodyContent = retBody;

        } catch(error) {
            console.log('ERRO: ' + error);
        }

        return bodyContent;
    },
    userResDecorator: function(proxyRes, proxyResData, req, res) {
        return proxyResData;
    },
    proxyReqPathResolver: function(req) {
        return '/saga/autocadastro';
    }
});


const clientesServiceProxy = httpProxy('http://localhost:8083/clientes', {
    proxyReqOptDecorator: function(proxyReqOpts, srcReq) {

        proxyReqOpts.headers['Content-Type'] = 'application/json';
        proxyReqOpts.method = 'GET';

        return proxyReqOpts;
    },


    proxyReqBodyDecorator: function(bodyContent, srcReq) {

        return bodyContent;
    },


    userResDecorator: function(proxyRes, proxyResData, req, res) {
      
        return proxyResData;
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

            retBody.email = bodyContent.email;
            retBody.nome = bodyContent.nome;
            retBody.telefone = bodyContent.telefone;
            bodyContent = retBody;

        } catch(error) {
            console.log('ERRO: ' + error);
        };

        return bodyContent;
    },

    userResDecorator: function(proxyRes, proxyResData, req, res) {
        const data = JSON.parse(proxyResData.toString('utf8'));
        return data;
    }
});



const contaServiceProxy = httpProxy("http://localhost:5000/contas", {
    proxyReqOptDecorator: function(proxyReqOpts, srcReq) {
        proxyReqOpts.headers['Content-Type'] = 'application/json';
        proxyReqOpts.method = 'POST';
        return proxyReqOpts;
    },

    proxyReqBodyDecorator: function(bodyContent, srcReq) {
        
        return bodyContent;
    },

    userResDecorator: function(proxyRes, proxyResData, req, res) {
        const data = JSON.parse(proxyResData.toString('utf8'));
        return JSON.stringify(data);
    }
});



const contaRejeitarProxy = httpProxy("http://localhost:8081/contas/rejeitar-cliente/:id", {

    proxyReqOptDecorator: (proxyReqOpts, srcReq) => {
        proxyReqOpts.headers['Content-Type'] = 'application/json';
        proxyReqOpts.method = 'PUT';

        return proxyReqOpts;

    },

    proxyReqBodyDecorator: (bodyContent, srcReq) => {
        return bodyContent;
    },

    userResDecorator: (proxyRes, proxyResData, req, res) => {
        const data = JSON.parse(proxyResData.toString('utf8'));
        return JSON.stringify(data);
    }
});



const contaTransferenciaProxy = httpProxy("http://localhost:8081/contas/transferencia/:id", {

    proxyReqOptDecorator: (proxyReqOpts, srcReq) => {
        proxyReqOpts.headers['Content-Type'] = 'application/json';
        proxyReqOpts.method = 'POST';
        return proxyReqOpts;

    },

    proxyReqBodyDecorator: (bodyContent, srcReq) => {

        const retBody = {};

        retBody.data = bodyContent.data;
        retBody.tipo = bodyContent.tipo;
        retBody.origem = bodyContent.origem;
        retBody.destino = bodyContent.destino;
        retBody.valor = bodyContent.valor;

        return bodyContent;
    },

    userResDecorator: (proxyRes, proxyResData, req, res) => {
        const data = JSON.parse(proxyResData.toString('utf8'));
        return JSON.stringify(data);
    }
});


const contaSaqueProxy = httpProxy("http://localhost:8081/contas/saques/:id", {

    proxyReqOptDecorator: (proxyReqOpts, srcReq) => {
        proxyReqOpts.headers['Content-Type'] = 'application/json';
        proxyReqOpts.method = 'POST';

        const retBody = {};

        retBody.data = bodyContent.data;
        retBody.tipo = bodyContent.tipo;
        retBody.origem = bodyContent.origem;
        retBody.destino = bodyContent.destino;
        retBody.valor = bodyContent.valor;

        return bodyContent;

    },

    userResDecorator: (proxyRes, proxyResData, req, res) => {
        const data = JSON.parse(proxyResData.toString('utf8'));
        return JSON.stringify(data);
    }
});



const contaDepositoProxy = httpProxy("http://localhost:8081/contas/saques/:id", {

    proxyReqOptDecorator: (proxyReqOpts, srcReq) => {
        proxyReqOpts.headers['Content-Type'] = 'application/json';
        proxyReqOpts.method = 'POST';

        const retBody = {};

        retBody.data = bodyContent.data;
        retBody.tipo = bodyContent.tipo;
        retBody.origem = bodyContent.origem;
        retBody.destino = bodyContent.destino;
        retBody.valor = bodyContent.valor;

        return retBody;

    },

    userResDecorator: (proxyRes, proxyResData, req, res) => {
        const data = JSON.parse(proxyResData.toString('utf8'));
        return JSON.stringify(data);
    }
});



const contaExtratoProxy = httpProxy("http://localhost:8081/contas/extrato/:id", {

    proxyReqOptDecorator: (proxyReqOpts, srcReq) => {
        proxyReqOpts.headers['Content-Type'] = 'application/json';
        proxyReqOpts.method = 'GET';

        const retBody = {};

        retBody.dataInicio = bodyContent.dataInicio;
        retBody.dataFim = bodyContent.dataFim;
        

        return retBody;

    },

    userResDecorator: (proxyRes, proxyResData, req, res) => {
        const data = JSON.parse(proxyResData.toString('utf8'));
        return JSON.stringify(data);
    }
});



app.put('/conta/rejeitar-cliente/:id', (req, res, next) => {
    
    contaRejeitarProxy(req, res, next);
});


app.post('/conta/transferencia/:id', (req, res, next) => {
    contaTransferenciaProxy(req, res, next);
});


app.post('/conta/saques/:id', (req, res, next) => {
    contaSaqueProxy(req, res, next);
});


app.post('/conta/deposito/:id', (req, res, next) => {
    contaDepositoProxy(req, res, next);
});


app.get('/conta/extrato/', (req, res, next) => {
    contaExtratoProxy(req, res, next);
})



const gerenteGetProxy = httpProxy("http://localhost:8082/gerente", {

    proxyReqOptDecorator: function(proxyReqOpts, srcReq) {
        proxyReqOpts.headers['Content-Type'] = 'application/json';
        proxyReqOpts.method = 'POST';
        return proxyReqOpts;
    },

    proxyReqBodyDecorator: function(bodyContent, srcReq) {
        
        return bodyContent;
    },

    userResDecorator: function(proxyRes, proxyResData, req, res) {
        const data = JSON.parse(proxyResData.toString('utf8'));
        return JSON.stringify(data);
    }
});



const gerenteUpdateProxy = httpProxy("http://localhost:8082/gerente/:id", {
    
    proxyReqOptDecorator: function(proxyReqOpts, srcReq) {
        proxyReqOpts.headers['Content-Type'] = 'application/json';
        proxyReqOpts.method = 'PUT';

        
        const retBody = {};

        retBody.tipo = bodyContent.tipo;
        retBody.email = bodyContent.email;
        retBody.telefone = bodyContent.telefone;
        
        return retBody;

    },

    proxyReqBodyDecorator: function(bodyContent, srcReq) {
        
        return bodyContent;
    },

    userResDecorator: function(proxyRes, proxyResData, req, res) {
        const data = JSON.parse(proxyResData.toString('utf8'));
        return JSON.stringify(data);
    }
});





const gerenteDeleteProxy = httpProxy("http://localhost:8082/gerente/:id", {
    
    proxyReqOptDecorator: function(proxyReqOpts, srcReq) {
        proxyReqOpts.headers['Content-Type'] = 'application/json';
        proxyReqOpts.method = 'DELETE';

        const retBody = {};

        retBody.tipo = bodyContent.tipo;
        retBody.email = bodyContent.email;
        retBody.telefone = bodyContent.telefone;
        
        return retBody;

    },

    proxyReqBodyDecorator: function(bodyContent, srcReq) {
        
        return bodyContent;
    },

    userResDecorator: function(proxyRes, proxyResData, req, res) {
        const data = JSON.parse(proxyResData.toString('utf8'));
        return JSON.stringify(data);
    }
});



const gerenteGetAllProxy = httpProxy("http://localhost:8082/gerente/", {
    
    proxyReqOptDecorator: function(proxyReqOpts, srcReq) {
        proxyReqOpts.headers['Content-Type'] = 'application/json';
        proxyReqOpts.method = 'GET';
    },

    proxyReqBodyDecorator: function(bodyContent, srcReq) {
        
        return bodyContent;
    },

    userResDecorator: function(proxyRes, proxyResData, req, res) {
        const data = JSON.parse(proxyResData.toString('utf8'));
        return JSON.stringify(data);
    }
});



const gerentePostProxy = httpProxy("http://localhost:8082/gerente/:id", {
    
    proxyReqOptDecorator: function(proxyReqOpts, srcReq) {
        proxyReqOpts.headers['Content-Type'] = 'application/json';
        proxyReqOpts.method = 'POST';

        const retBody = {};

        retBody.tipo = bodyContent.tipo;
        retBody.email = bodyContent.email;
        retBody.telefone = bodyContent.telefone;
        
        return retBody;

    },

    proxyReqBodyDecorator: function(bodyContent, srcReq) {
        
        return bodyContent;
    },

    userResDecorator: function(proxyRes, proxyResData, req, res) {
        const data = JSON.parse(proxyResData.toString('utf8'));
        return JSON.stringify(data);
    }
});


const gerenteTelaInicialProxy = httpProxy("http://localhost:8082/gerente/:id", {
    
    proxyReqOptDecorator: function(proxyReqOpts, srcReq) {
        proxyReqOpts.headers['Content-Type'] = 'application/json';
        proxyReqOpts.method = 'GET';

        
        return retBody;

    },

    proxyReqBodyDecorator: function(bodyContent, srcReq) {
        
        return bodyContent;
    },

    userResDecorator: function(proxyRes, proxyResData, req, res) {
        const data = JSON.parse(proxyResData.toString('utf8'));
        return JSON.stringify(data);
    }
});


const gerenteRelatorioProxy = httpProxy("http://localhost:8082/gerente/:id", {
    
    proxyReqOptDecorator: function(proxyReqOpts, srcReq) {
        proxyReqOpts.headers['Content-Type'] = 'application/json';
        proxyReqOpts.method = 'GET';

        
        return retBody;

    },

    proxyReqBodyDecorator: function(bodyContent, srcReq) {
        
        return bodyContent;
    },

    userResDecorator: function(proxyRes, proxyResData, req, res) {
        const data = JSON.parse(proxyResData.toString('utf8'));
        return JSON.stringify(data);
    }
});


app.get('/gerentes/consultar/:id', (req, res, next) => {
    gerenteGetProxy(req, res, next);
});


app.put('/gerentes/update/:id', (req, res, next) => {
    console.log(req.body)
    gerenteUpdateProxy(req, res, next);
});


app.delete('/gerentes/delete/:id', (req, res, next) => {
    gerenteDeleteProxy(req, res, next);
});


app.get('/gerentes/consultaAll/', (req, res, next) => {
    gerenteGetAllProxy(req, res, next);
});


app.post('/gerentes/newGerente/', (req, res, next) => {
    gerentePostProxy(req, res, next);

});


app.get('/gerentes/telaInicial/', (req, res, next) => {
    gerenteTelaInicialProxy(req, res, next);
});


app.get('/gerentes/relatorio/', (req, res, next) => {
    gerenteRelatorioProxy(req, res, next);
});



const sagaUpdatePerfil = httpProxy("http://localhost:8084/updatePerfil/:id", {
    
    proxyReqOptDecorator: function(proxyReqOpts, srcReq) {
        proxyReqOpts.headers['Content-Type'] = 'application/json';
        proxyReqOpts.method = 'PUT';

        const retBody = {};

        retBody.cpf = bodyContent.cpf;
        retBody.email = bodyContent.email;
        retBody.nome = bodyContent.nome;
        retBody.telefone = bodyContent.telefone;

        retBody.salario = bodyContent.salario;
        retBody.logradouro = bodyContent.logradouro;
        retBody.numero = bodyContent.numero;
        retBody.complemento = bodyContent.complemento;

        retBody.cep = bodyContent.cep;
        retBody.cidade = bodyContent.cidade;
        retBody.uf = bodyContent.uf;

        retBody.estado = bodyContent.estado;

        
        return retBody;

    },

    proxyReqBodyDecorator: function(bodyContent, srcReq) {
        
        return bodyContent;
    },

    userResDecorator: function(proxyRes, proxyResData, req, res) {
        const data = JSON.parse(proxyResData.toString('utf8'));
        return JSON.stringify(data);
    }

});




const sagaInserirGerente = httpProxy("http://localhost:8084/inserirGerente/", {
    
    proxyReqOptDecorator: function(proxyReqOpts, srcReq) {
        proxyReqOpts.headers['Content-Type'] = 'application/json';
        proxyReqOpts.method = 'POST';

        const retBody = {};

        retBody.cpf = bodyContent.cpf;
        retBody.email = bodyContent.email;
        retBody.telefone = bodyContent.telefone;
        
        return retBody;

    },

    proxyReqBodyDecorator: function(bodyContent, srcReq) {
        
        return bodyContent;
    },

    userResDecorator: function(proxyRes, proxyResData, req, res) {
        const data = JSON.parse(proxyResData.toString('utf8'));
        return JSON.stringify(data);
    }

});




const sagaDeletarGerente = httpProxy("http://localhost:8084/deletarGerente/:id", {
    
    proxyReqOptDecorator: function(proxyReqOpts, srcReq) {
        proxyReqOpts.headers['Content-Type'] = 'application/json';
        proxyReqOpts.method = 'PUT';

    },

    proxyReqBodyDecorator: function(bodyContent, srcReq) {
        return bodyContent;
    },

    userResDecorator: function(proxyRes, proxyResData, req, res) {
        const data = JSON.parse(proxyResData.toString('utf8'));
        return JSON.stringify(data);
    }

});



app.delete('/deletarGerente/:id', (req, res, next) => {
    sagaDeletarGerente(req, res, next);
});


app.post("/inserirGerente/", (req, res, next) => {
    sagaInserirGerente(req, res, next);
})


app.put('/atualizarPerfil/:id', (req, res, next) => {
    console.log(req.body)
    sagaUpdatePerfil(req, res, next);
});


app.delete('/gerentes/delete/:id', (req, res, next) => {
    gerenteDeleteProxy(req, res, next);
});


app.get('/gerentes/consultaAll/', (req, res, next) => {
    gerenteGetAllProxy(req, res, next);
});


app.post('/gerentes/newGerente/', (req, res, next) => {
    gerentePostProxy(req, res, next);

});


app.get('/gerentes/telaInicial/', (req, res, next) => {
    gerenteTelaInicialProxy(req, res, next);
});


app.get('/gerentes/relatorio/', (req, res, next) => {
    gerenteRelatorioProxy(req, res, next);
});



const authUpdateProxy = httpProxy("http://localhost:8080/auth/update", {

    proxyReqOptDecorator: function(proxyReqOpts, srcReq) {
        proxyReqOpts.headers['Content-Type'] = 'application/json';
        proxyReqOpts.method = 'PUT';
        return proxyReqOpts;
    },

    proxyReqBodyDecorator: function(bodyContent, srcReq) {
        try {

            let retBody = {};

            retBody.email = bodyContent.email;
            retBody.senha = bodyContent.senha;
            retBody.tipo = bodyContent.tipo;
            retBody.active = bodyContent.active;
            
      
            bodyContent = retBody;

        } catch(error) {
            console.log('ERRO: ' + error);
        }

        return bodyContent;
    },

    userResDecorator: function(proxyRes, proxyResData, req, res) {

        if (proxyRes.statusCode === 200) {

            res.status(200);
 
            return {message: "Usuário atualizado!!", status: 200};
 
         } else if (proxyRes.statusCode === 404){
             res.status(404);
             return {message: 'Usuário não encontrado!', status: 404};
 
         } else {
             return {message: 'Erro ao atualizaro login', status: proxyRes.statusCode};
         }
    }
});



const authAprovarProxy = httpProxy("http://localhost:8080/auth", {
    proxyReqOptDecorator: function(proxyReqOpts, srcReq) {
        proxyReqOpts.headers['Content-Type'] = 'application/json';
        proxyReqOpts.method = 'PUT';
        return proxyReqOpts;
    },

    proxyReqBodyDecorator: function(bodyContent, srcReq) {
      
        return bodyContent;
    },

    userResDecorator: function(proxyRes, proxyResData, req, res) {

        if (proxyRes.statusCode === 200) {

           res.status(200);

           return {message: "Login aprovado!", status: 200, body: ""};

        } else if (proxyRes.statusCode === 404){
            res.status(404);
            return {message: 'Login não encontrado!', status: 404};

        } else {
            return {message: 'Erro ao aprovar o login', status: proxyRes.statusCode};
        }
    }
});


const authRegistrarProxy = httpProxy("http://localhost:8080/auth/registrar", {
    proxyReqOptDecorator: function(proxyReqOpts, srcReq) {
        proxyReqOpts.headers['Content-Type'] = 'application/json';
        proxyReqOpts.method = 'POST';
        
        console.log(proxyReqOpts)
        return proxyReqOpts;
    },

    proxyReqBodyDecorator: function(bodyContent, srcReq) {
        try {
            let retBody = {};

            retBody.email = bodyContent.email;
            retBody.tipo = bodyContent.tipo;
            
            bodyContent = retBody;

            console.log(bodyContent);

        } catch(error) {
            console.log('ERRO: ' + error);
        }

        return bodyContent;
    },

    userResDecorator: function(proxyRes, proxyResData, req, res) {

        if (proxyRes.statusCode === 200) {

         res.status(200);
         return {message: "Login registrado com sucesso !", status: 200}

        } else if (res.statusCode === 409) {

            res.status(409);
            return {message: "Login já existente", status: 409}
        }
        
        else {
            res.status(proxyRes.statusCode);
            return {message: "Erro ao cadastrar o login"};
        }
    }
});



const authDeleteProxy = httpProxy("http://localhost:8080/auth", {
    proxyReqOptDecorator: function(proxyReqOpts, srcReq) {
        proxyReqOpts.headers['Content-Type'] = 'application/json';
        proxyReqOpts.method = 'DELETE';
        return proxyReqOpts;
    },

    proxyReqBodyDecorator: function(bodyContent, srcReq) {
       
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
            retBody.senha = bodyContent.senha;
            
      
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


app.post('/auth/autenticar', (req, res, next) => {
    authServiceProxy(req, res, next);
});


app.post('/auth/registrar', (req, res, next) => {
    console.log(req.body)
    authRegistrarProxy(req, res, next);
});


app.put('/auth/aprovar/:email', (req, res, next) => {
    authAprovarProxy(req, res, next);
});


app.delete('/auth/delete/:email', (req, res, next) => {
    authDeleteProxy(req, res, next);
});


app.put('/auth/update/:email', (req, res, next) => {
    authUpdateProxy(req, res, next);
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


app.get('/cliente/find/:uuid', (req, res, next) => {
    clientesServiceProxy(req, res, next);
});


app.get('/cliente/tela-inicial/:uuid', verifyJWT, (req, res, next) => {
    clientesServiceProxy(req, res, next);
});


app.delete('/cliente/delete/:uuid', (req, res, next) => {
    clientesServiceProxy(req, res, next);
});


// ========== Conta ========


app.get('/conta', verifyJWT, (req, res, next) => {
    contaServiceProxy(req, res, next);
});

app.put('/conta/rejeitar-cliente/:id', (req,res,next) => 
    contasProxy(req,res,next));

app.get('/saque/:uuid', verifyJWT, (req, res, next) => {
    contaServiceProxy(req, res, next);
});


app.get('/deposito/:uuid', verifyJWT, (req, res, next) => {
    contaServiceProxy(req, res, next);
});


app.get('/transferencia/:uuid', verifyJWT, (req, res, next) => {
    contaServiceProxy(req, res, next);
})


// ============= gerente ===========

app.get('/gerentes', verifyJWT, (req, res, next) => {
    gerentesServiceProxy(req, res, next);
});


app.get('/:uuid', verifyJWT, (req, res, next) => {
    gerentesServiceProxy(req, res, next);
});


app.put('/:uuid', verifyJWT, (req, res, next) => {
    gerentesServiceProxy(req, res, next);
});


app.delete('/:uuid', verifyJWT, (req, res, next) => {
    gerentesServiceProxy(req, res, next);
});

// ============ Autocadastro ===========

app.post('/autocadastro', (req, res, next) => {
    console.log(req)
    sagaAutocadastroProxy(req, res, next);
});



var server = http.createServer(app);
server.listen(3000, () => {console.log("Gateway up")});
