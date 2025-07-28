import { defineConfig } from 'vitepress'

// https://vitepress.dev/reference/site-config
export default defineConfig({
  title: "RClaim",
  description: "RClaim docs",
  themeConfig: {
    nav: [
      { text: 'Home', link: '/' },
    ],

    sidebar: [
      {
        text: 'Interactions',
        items: [
          { text: 'Introduction', link: '/introduction' },
          {text: "Commands", link: "commands"},
          {text: "Permissions", link: "permissions"},
          {text: "PlaceHolders", link: "placeholders"},
          {text: "API", items: [
            { text: 'RClaim', link: '/api/installation' },
            { text: "Events", link: '/api/events' },
            { text: "RClaimProvider", link: '/api/rclaimprovider' },
          ]}
        ]
      }
    ],

    socialLinks: [
      { icon: 'github', link: 'https://github.com/Weesli/RClaim' },
      {icon: 'discord', link: 'https://discord.gg/vBY6pzXW'}
    ]
  }
})
