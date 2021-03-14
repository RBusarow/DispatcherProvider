module.exports = {
  title: "Dispatch",
  tagline: "Quality of life coroutine utilities",
  url: "https://rbusarow.github.io/Dispatch/",
  baseUrl: "/",
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
          docId: "changelog",
          label: "Docs",
          position: "left",
        },
        {
          type: "docsVersionDropdown",
          position: "right",
          dropdownActiveClassDisabled: true,
          dropdownItemsAfter: [
            {
              // to: "/versions",
              // label: "All versions",
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
