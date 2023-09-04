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

    daisyui: {
        themes: ["light", "dark", "cupcake"],
    },

    plugins: [
        require("@tailwindcss/typography"),
        require("daisyui")
    ],
}
