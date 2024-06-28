const sharp = require('sharp');

const processImage = async (inputBuffer, format, width, height, compress) => {
    let image = sharp(inputBuffer);

    if (format) {
        image = image.toFormat(format);
    }

    if (width || height) {
        image = image.resize(width, height);
    }

    const { data, info } = await image.toBuffer({ resolveWithObject: true });

    return {
        buffer: data,
        width: info.width,
        height: info.height,
        size: info.size
    };
};

const compressImage = async (inputBuffer, quality) => {
    let image = sharp(inputBuffer);

    // 압축 품질 설정
    image = image.jpeg({ quality: quality }).png({ quality: quality }).webp({ quality: quality });

    const { data, info } = await image.toBuffer({ resolveWithObject: true });

    return {
        buffer: data,
        width: info.width,
        height: info.height,
        size: info.size
    };
};

const args = process.argv.slice(2);
const command = args[0];
const format = args[1];
const width = args[2] ? parseInt(args[2]) : null;
const height = args[3] ? parseInt(args[3]) : null;
const compress = args[4] === 'true';
const quality = args[5] ? parseInt(args[5]) : 80;

let inputBuffer = Buffer.alloc(0);

// 스트림을 통해 이미지를 수신
process.stdin.on('data', chunk => {
    inputBuffer = Buffer.concat([inputBuffer, chunk]);
});

process.stdin.on('end', () => {
    if (command === 'process') {
        processImage(inputBuffer, format, width, height, compress)
            .then(result => {
                process.stdout.write(JSON.stringify(result));
            })
            .catch(error => {
                process.stderr.write(JSON.stringify({ error: error.message }));
            });
    } else if (command === 'compress') {
        compressImage(inputBuffer, quality)
            .then(result => {
                process.stdout.write(JSON.stringify(result));
            })
            .catch(error => {
                process.stderr.write(JSON.stringify({ error: error.message }));
            });
    }
});
