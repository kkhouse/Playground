const http = require('http');
const fs = require('fs');
const url = require('url');
const qs = require('querystring');
const ejs = require('ejs');

const INDEX_PAGE = fs.readFileSync('./index.ejs', 'utf8');

/**
 * クッキーの値を取得
 */
function getCookie(key, request) {
  const cookieData = request.headers.cookie !== undefined ? request.headers.cookie : '';
  const datas = cookieData.split(';').map(data => data.trim());
  const msgKeyValue = datas.find(data => data.startsWith(`${key}=`));
  if (msgKeyValue === undefined) return '';
  const msgValue = msgKeyValue.replace(`${key}=`, '');
  return unescape(msgValue);
}

/**
 * クッキーの値を設定
 */
function setCookie(key, value, response) {
  const escapedValue = escape(value);
  response.setHeader('Set-Cookie', [`${key}=${escapedValue}`]);
}

function getFromClient(request, response) {
  const urlParts = url.parse(request.url, true);
  const pathName = urlParts.pathname;
  switch (pathName) {
    case '/':
      if (request.method === 'POST') {
        let body = '';
        request.on('data', data => {
          body += data;
        });
        request.on('end', () => {
          const postData = qs.parse(body);
          setCookie('userName', postData.userName, response);
          response.writeHead(303, { Location: '/' });
          response.end();
        });
      } else {
        const userNameFromCookie = getCookie('userName', request);
        const userName = userNameFromCookie ? userNameFromCookie : 'ゲスト';
        const content = ejs.render(INDEX_PAGE, {
          title: 'Index',
          userName
        });
        response.writeHead(200, { 'Content-Type': 'text/html' });
        response.write(content);
        response.end();
      }
      break;
    default:
      response.writeHead(200, { 'Content-Type': 'text/plain' });
      response.write('No page...');
      response.end();
      break;
  }
}

const server = http.createServer(getFromClient);
server.listen(3000);
console.log('====== Server start!! ======');