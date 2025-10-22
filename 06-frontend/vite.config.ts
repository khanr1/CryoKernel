import { defineConfig } from "vite";
import scalaJSPlugin from "@scala-js/vite-plugin-scalajs";

export default defineConfig({
    plugins: [
        scalaJSPlugin({
            projectID: "frontend", // must match your SBT project id: lazy val frontend = ...
            cwd: ".."              // Vite runs in 06-frontend; sbt build is one level up
        })
    ],
    server: {
        port: 5173,
        // proxy: { "/api": "http://localhost:8080" } // uncomment if you want API proxy
    },
    build: {
        outDir: "dist",
        emptyOutDir: true
    }
});