import { spawnSync } from "child_process";
import { defineConfig } from "vite";

function isDev() {
  return process.env.NODE_ENV !== "production";
}

function printSbtTask(task) {
  const args = ["--error", "--batch", `print ${task}`];
  const options = {
    stdio: [
      "pipe", // StdIn.
      "pipe", // StdOut.
      "inherit", // StdErr.
    ],
  };
  const result = process.platform === 'win32'
    ? spawnSync("sbt.bat", args.map(x => `"${x}"`), {shell: true, ...options})
    : spawnSync("sbt", args, options);

  if (result.error)
    throw result.error;
  if (result.status !== 0)
    throw new Error(`sbt process failed with exit code ${result.status}`);
  let data = result.stdout.toString('utf8');
  console.log("--- before ---")
  let ret = data.substring(0, data.indexOf('\n')).trim();
  console.log(data.split(''))
  console.log(ret.split(''))
  console.log("--- after ---")
  return ret;
}

const replacementForPublic = isDev()
  ? printSbtTask("publicDev")
  : printSbtTask("publicProd");

export default defineConfig({
  build: {
    sourcemap: true,
    outDir: "backend/src/universal/static"
  },
  resolve: {
    alias: [
      {
        find: "@public",
        replacement: replacementForPublic,
      },
    ],
  }
});
