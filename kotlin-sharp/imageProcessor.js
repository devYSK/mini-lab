const sharp = require('sharp');

const processImage = async (inputBuffer, format, width, height, compress) => {
    console.log('Processing image...');
    let image = sharp(inputBuffer);

    if (format) {
        image = image.toFormat(format);
    }

    if (width || height) {
        image = image.resize(width, height);
    }

    const { data, info } = await image.toBuffer({ resolveWithObject: true });

    return {
        buffer: data.toString('base64'),
        width: info.width,
        height: info.height,
        size: info.size
    };
};

const compressImage = async (inputBuffer, quality) => {
    console.log('Compressing image...');
    let image = sharp(inputBuffer);

    // 압축 품질 설정
    image = image.jpeg({ quality: quality }).png({ quality: quality }).webp({ quality: quality });

    const { data, info } = await image.toBuffer({ resolveWithObject: true });

    return {
        buffer: data.toString('base64'),
        width: info.width,
        height: info.height,
        size: info.size
    };
};

const args = process.argv.slice(2);
const command = args[0];
const inputBase64 = args[1];
const format = args[2];
const width = args[3] ? parseInt(args[3]) : null;
const height = args[4] ? parseInt(args[4]) : null;
const compress = args[5] === 'true';
const quality = args[6] ? parseInt(args[6]) : 80;

const inputBuffer = Buffer.from(inputBase64, 'base64');

if (command === 'process') {
    processImage(inputBuffer, format, width, height, compress)
        .then(result => {
            console.log(JSON.stringify(result));
        })
        .catch(error => {
            console.error(JSON.stringify({ error: error.message }));
        });
} else if (command === 'compress') {
    compressImage(inputBuffer, quality)
        .then(result => {
            console.log(JSON.stringify(result));
        })
        .catch(error => {
            console.error(JSON.stringify({ error: error.message }));
        });
}
