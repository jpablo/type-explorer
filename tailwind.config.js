/** @type {import('tailwindcss').Config} */
module.exports = {
    important: true,
    content: [
        './index.html',
        './ui/target/scala-3.3.0-RC3/ui-fastopt/main.js'
    ],
    theme: {
        extend: {},
    },
    plugins: [
        require("daisyui")
    ],
}
