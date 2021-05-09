/*
 * Copyright (C) 2021 Rick Busarow
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

module.exports = {
  title: "Dispatch",
  tagline: "Quality of life coroutine utilities",
  url: "https://rbusarow.github.io/",
  baseUrl: "/Dispatch/",
  onBrokenLinks: "throw",
  onBrokenMarkdownLinks: "warn",
  favicon: "img/favicon.ico",
  organizationName: "rbusarow", // Usually your GitHub org/user name.
  projectName: "Dispatch", // Usually your repo name.
  themeConfig: {
    hideableSidebar: true,
    colorMode: {
      defaultMode: "light",
      disableSwitch: false,
      respectPrefersColorScheme: true,
    },
    announcementBar: {
      id: "supportus",
      content:
        '⭐️ If you like Dispatch, give it a star on <a target="_blank" rel="noopener noreferrer" href="https://github.com/rbusarow/Dispatch/">GitHub</a>! ⭐️',
    },
    navbar: {
      title: "Dispatch",
      //      logo: {
      //        alt: 'Dispatch Logo',
      //        src: 'img/logo.svg',
      //      },
      items: [
        {
          type: "doc",
          docId: "quickstart",
          label: "Basics",
          position: "left",
        },
        {
          type: "doc",
          docId: "modules/dispatch-core",
          label: "Modules",
          position: "left",
        },
        {
          type: "docsVersionDropdown",
          position: "right",
          dropdownActiveClassDisabled: true,
          dropdownItemsAfter: [
            {
//               to: "/versions",
//               label: "All versions",
            },
          ],
        },
        {
          label: "Twitter",
          href: "https://twitter.com/rbusarow",
          position: "right",
        },
        {
          href: "https://github.com/rbusarow/Dispatch",
          label: "GitHub",
          position: "right",
        },
      ],
    },
    footer: {
      copyright: `Copyright © ${new Date().getFullYear()} Rick Busarow, Built with Docusaurus.`,
    },
    prism: {
      theme: require("prism-react-renderer/themes/github"),
      darkTheme: require("prism-react-renderer/themes/dracula"),
      additionalLanguages: ["kotlin", "groovy"],
    },
  },
  presets: [
    [
      "@docusaurus/preset-classic",
      {
        docs: {
          sidebarPath: require.resolve("./sidebars.js"),
          // Please change this to your repo.
          editUrl: "https://github.com/rbusarow/Dispatch/",
        },
        blog: {
          showReadingTime: true,
          // Please change this to your repo.
          editUrl: "https://github.com/rbusarow/Dispatch/",
        },
        theme: {
          customCss: require.resolve("./src/css/custom.css"),
        },
      },
    ],
  ],
};
