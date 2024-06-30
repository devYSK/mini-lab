const sharp = require('sharp');

const processImage = async (inputBuffer, options) => {
    let image = sharp(inputBuffer, { failOnError: false });

    if (options.width || options.height) {
        image = image.resize({
            width: options.width,
            height: options.height,
            fit: sharp.fit.inside,
            withoutEnlargement: true // 이미지가 원본보다 커지지 않도록 합니다.
        });
    }

    const format = options.format ? (options.format === 'jpg' ? 'jpeg' : options.format) : 'jpeg';

    if (options.compress) {
        switch (format) {
            case 'jpeg':
                image = image.jpeg({ quality: options.quality, mozjpeg: true });
                break;
            case 'png':
                image = image.png({ quality: options.quality, compressionLevel: 9 });
                break;
            case 'webp':
                image = image.webp({ quality: options.quality, lossless: false });
                break;
            default:
                throw new Error(`Error processing format ${format}`);
        }
    }

    const { data, info } = await image.toBuffer({ resolveWithObject: true });

    return {
        buffer: data,
        width: info.width,
        height: info.height,
        size: data.length
    };
};

const handleCommand = async (command, inputBuffer, options) => {
    try {
        let result;
        if (command === 'process' || command === 'compress') {
            result = await processImage(inputBuffer, options);
        } else {
            throw new Error('Unknown command');
        }
        process.stdout.write(JSON.stringify(result));
    } catch (error) {
        process.stderr.write(JSON.stringify({ error: error.message }));
    }
};

const args = process.argv.slice(2);
const command = args[0];
const options = {
    format: args[1],
    width: args[2] ? parseInt(args[2]) : null,
    height: args[3] ? parseInt(args[3]) : null,
    compress: args[4] === 'true',
    quality: args[5] ? parseInt(args[5]) : 80
};

let inputBuffer = Buffer.alloc(0);
// 스트림을 통해 이미지를 수신
process.stdin.on('data', chunk => {
    inputBuffer = Buffer.concat([inputBuffer, chunk]);
});

process.stdin.on('end', () => {
    handleCommand(command, inputBuffer, options);
});
