/** @type {import('tailwindcss').Config} */
module.exports = {
    important: true,
    content: [
        './index.html',
        './ui/target/scala-*/**/*.js'
    ],
    theme: {
        extend: {},
    },
    plugins: [
        require("@tailwindcss/typography"),
        require("daisyui")
    ],
}
