# language: pt
Funcionalidade: Validar contratos da API EcoTrack com BDD
  A fim de garantir integridade, rastreabilidade e indicadores ESG
  Como equipe de qualidade
  Quero verificar operações REST em situações positivas e negativas

  Cenário: Registro válido
    Dado que a API EcoTrack está disponível
    Quando cadastro uma nova conta válida
    Então o status HTTP deve ser 201
    E a resposta segue o contrato "auth"
    E o token JWT está presente

  Cenário: Login válido
    Dado que possuo uma conta autenticada
    Quando realizo login com minhas credenciais
    Então o status HTTP deve ser 200
    E a resposta segue o contrato "auth"

  Cenário: Login inválido
    Dado que possuo uma conta autenticada
    Quando tento login com senha incorreta
    Então o status HTTP deve ser 400
    E a resposta segue o contrato "error"

  Cenário: Registro duplicado
    Dado que possuo uma conta autenticada
    Quando tento cadastrar a mesma conta novamente
    Então o status HTTP deve ser 400
    E a resposta segue o contrato "error"

  Cenário: Cadastro de resíduo e cálculo de CO2
    Dado que possuo uma conta autenticada
    Quando cadastro um resíduo válido
    Então o status HTTP deve ser 201
    E a resposta segue o contrato "residuo"
    E o cálculo de CO2 evitado corresponde a 18.0 kg

  Cenário: Listagem de resíduos
    Dado que possuo uma conta autenticada
    E que cadastrei um resíduo válido
    Quando consulto a lista de resíduos
    Então o status HTTP deve ser 200
    E a lista de resíduos segue o contrato JSON Schema

  Cenário: Consulta de resíduo por ID
    Dado que possuo uma conta autenticada
    E que cadastrei um resíduo válido
    Quando consulto o resíduo por identificador
    Então o status HTTP deve ser 200
    E a resposta segue o contrato "residuo"

  Cenário: Atualização de resíduo
    Dado que possuo uma conta autenticada
    E que cadastrei um resíduo válido
    Quando atualizo o resíduo
    Então o status HTTP deve ser 200
    E a resposta segue o contrato "residuo"

  Cenário: Exclusão de resíduo
    Dado que possuo uma conta autenticada
    E que cadastrei um resíduo válido
    Quando excluo o resíduo
    Então o status HTTP deve ser 200
    E o texto confirma a exclusão do resíduo

  Cenário: Peso de resíduo inválido
    Dado que possuo uma conta autenticada
    Quando tento cadastrar um resíduo com peso zero
    Então o status HTTP deve ser 400
    E a resposta segue o contrato "error"

  Cenário: Resíduo inexistente
    Dado que possuo uma conta autenticada
    Quando busco um resíduo inexistente
    Então o status HTTP deve ser 404
    E a resposta segue o contrato "error"

  Cenário: Acesso sem JWT
    Dado que a API EcoTrack está disponível
    Quando acesso resíduos sem autenticação
    Então o status HTTP deve ser 403

  Cenário: Cadastro de coleta
    Dado que possuo uma conta autenticada
    E que cadastrei um resíduo válido
    Quando cadastro uma coleta para esse resíduo
    Então o status HTTP deve ser 201
    E a resposta segue o contrato "coleta"

  Cenário: Listagem de coletas
    Dado que possuo uma conta autenticada
    E que cadastrei um resíduo válido
    E que cadastrei uma coleta para esse resíduo
    Quando consulto a lista de coletas
    Então o status HTTP deve ser 200
    E a lista de coletas segue o contrato JSON Schema

  Cenário: Consulta de coleta por ID
    Dado que possuo uma conta autenticada
    E que cadastrei um resíduo válido
    E que cadastrei uma coleta para esse resíduo
    Quando consulto a coleta por identificador
    Então o status HTTP deve ser 200
    E a resposta segue o contrato "coleta"

  Cenário: Atualização de coleta
    Dado que possuo uma conta autenticada
    E que cadastrei um resíduo válido
    E que cadastrei uma coleta para esse resíduo
    Quando atualizo a coleta
    Então o status HTTP deve ser 200
    E a resposta segue o contrato "coleta"

  Cenário: Exclusão de coleta
    Dado que possuo uma conta autenticada
    E que cadastrei um resíduo válido
    E que cadastrei uma coleta para esse resíduo
    Quando excluo a coleta
    Então o status HTTP deve ser 204
    E a resposta não possui corpo

  Cenário: Coleta sem empresa
    Dado que possuo uma conta autenticada
    E que cadastrei um resíduo válido
    Quando tento cadastrar uma coleta sem empresa responsável
    Então o status HTTP deve ser 400
    E a resposta segue o contrato "error"

  Cenário: Indicadores ESG
    Dado que possuo uma conta autenticada
    Quando consulto os indicadores ESG
    Então o status HTTP deve ser 200
    E a resposta segue o contrato "indicadores"

  Cenário: Relatório ambiental
    Dado que possuo uma conta autenticada
    Quando gero o relatório ambiental
    Então o status HTTP deve ser 201
    E a resposta segue o contrato "relatorio"
