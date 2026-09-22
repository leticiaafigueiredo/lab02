import React from 'react';
import { Sidebar } from './Sidebar';
import { Header } from './Header';

export const AppLayout: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  return (
    <div className="flex min-h-screen bg-slate-50 font-sans antialiased text-slate-900">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0">
        <Header />
        <main className="flex-1 p-6 sm:p-8 max-w-7xl w-full mx-auto">{children}</main>
        <footer className="border-t border-slate-200/80 bg-white py-4 px-8 text-center text-xs text-slate-400">
          PUC Minas — SGA Acadêmico • Engenharia de Software
        </footer>
      </div>
    </div>
  );
};
