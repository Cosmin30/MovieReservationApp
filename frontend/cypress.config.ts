import { defineConfig } from "cypress";

export default defineConfig({
  e2e: {
    baseUrl: "http://localhost:4200",
    specPattern: "cypress/e2e/**/*.spec.ts",
    setupNodeEvents(on, config) {
    },
  },

  component: undefined,
});
