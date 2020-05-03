const filename = process.argv[2];
if (!filename) {
  throw 'Filename is not specified';
}

const fs = require('fs');
const os = require('os');
const crypto = require('crypto');

function insert(str, start, delCount, newSubStr) {
  return str.slice(0, start) + newSubStr + str.slice(start + delCount);
};

let data = fs.readFileSync(filename, 'utf8');
const matches = data.matchAll(/(#{2,6})(\s+)(<a name=".+"><\/a>)?(.+)(\r?\n)?/g);

const toc = [];

for (const match of Array.from(matches).reverse()) {
  const level = match[1];
  const spaces = match[2];
  const anchor = match[3];
  const title = match[4];
  const hash = crypto.createHash('md5').update(title).digest('hex');
  data = insert(data, match.index + level.length + spaces.length, anchor ? anchor.length : 0, `<a name="${hash}"></a>`);
  toc.unshift(`${'  '.repeat(level.length - 2)}* [${title}](#${hash})`);
}

const match = /#\s+.+(\r?\n)/.exec(data);
data = insert(data, match.index + match[0].length, 0, os.EOL + toc.join(os.EOL) + os.EOL);

fs.writeFileSync(filename, data, 'utf8');
